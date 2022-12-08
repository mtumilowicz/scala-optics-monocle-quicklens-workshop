package workshop.optics

import monocle.{Prism, _}
import monocle.macros.{GenLens, GenPrism}
import monocle.syntax.all._
import shared.hotel._
import shared.university._
import shared.user.PaymentMethod.PayPal
import shared.user.{Address, PaymentMethod, User}
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}
import Data._
import com.softwaremill.quicklens._

object OpticsSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("OpticsSpec")(
      test("monocle: user - set street number to 10; use GenLens") {
        val setStreetNumber = GenLens[Address](_.streetNumber)
        val setAddress = GenLens[User](_.address)
        val composed = setAddress andThen setStreetNumber
        val newStreetNo = 10
        val expectedResult = User("MT", Address(newStreetNo, "1012"), PaymentMethod.PayPal("m@gmail.com"))
        assertTrue(composed.replace(newStreetNo)(user) == expectedResult)
      },
      test("monocle: user - set street number to 10; use: focus") {
        val newStreetNo = 10
        val expectedResult = User("MT", Address(newStreetNo, "1012"), PaymentMethod.PayPal("m@gmail.com"))
        assertTrue(user.focus(_.address.streetNumber).replace(newStreetNo) == expectedResult)
      },
      test("monocle: university - increase each budget by 100; use: focus") {
        val modified = university
          .focus(_.departments)
          .each
          .modify(_.focus(_.budget).modify(_ + 100))
        val budgets = modified.departments.values.map(_.budget)
        assertTrue(budgets.forall(_ >= 100))
      },
      test("monocle: university - double budget only for computer science; use: focus") {
        val modified = university
          .focus(_.departments)
          .at("Computer Science")
          .some
          .modify(_.focus(_.budget).modify(_ * 200))
        val budget = modified.departments("Computer Science").budget
        val expectedBudget = 90
        assertTrue(budget >= expectedBudget)
      },
      test("monocle: university - replace head lecturer in History department; use: focus") {
        val expectedLecturer = Lecturer("luke", "skywalker", 1000)
        val modified = university
          .focus(_.departments)
          .at("History")
          .some
          .modify(_.focus(_.lecturers).index(0).replace(expectedLecturer))
        val headLecturer = modified.departments("History").lecturers.head
        assertTrue(headLecturer == expectedLecturer)
      },
      test("monocle: user - replace email in PayPal payment method; use: GenLens") {
        val newEmail = "newemail@gmail.com"
        val modified = GenLens[User](_.paymentMethod)
          .andThen(Prism.partial[PaymentMethod, String] { case PayPal(x) => x }(PayPal))
          .replace(newEmail)(user)
        assertTrue(modified.paymentMethod.asInstanceOf[PayPal].email == newEmail)
      },
      test("quicklens: user - replace email in PayPal payment method") {
        val newEmail = "newemail@gmail.com"
        val modified = user.modify(_.paymentMethod.when[PayPal].email).setTo(newEmail)
        assertTrue(modified.paymentMethod.asInstanceOf[PayPal].email == newEmail)
      },
      test("quicklens: hotel - set fee to 1 for all flexible rooms") {
        val newFee = 1
        val modified = hotel.modify(_.rooms.each.roomTariff.when[Flexible].fee).setTo(newFee)
        assertTrue(modified.rooms.head.roomTariff == hotel.rooms.head.roomTariff)
        assertTrue(modified.rooms(1).roomTariff == Flexible(newFee))
        assertTrue(modified.rooms(2).roomTariff == Flexible(newFee))
      },
      test("monocle: hotel - set fee to 1 for all flexible rooms; use: GenLens") {
        val newFee = 1

        val updatedHotel = GenLens[Hotel](_.rooms)
          .andThen(Traversal.fromTraverse[List, Room])
          .andThen(GenLens[Room](_.roomTariff))
          .andThen(Prism.partial[RoomTariff, BigDecimal] { case Flexible(x) => x }(Flexible))
          .replace(newFee)(hotel)

        assertTrue(updatedHotel.rooms.head.roomTariff == hotel.rooms.head.roomTariff)
        assertTrue(updatedHotel.rooms(1).roomTariff == Flexible(newFee))
        assertTrue(updatedHotel.rooms(2).roomTariff == Flexible(newFee))
      },
      test("monocle: modify details to details2") {
        val expectedDetails = "details2"
        val errorADetailsLens: Lens[ErrorA, String] = GenLens[ErrorA](_.details)
        val errorAPrism: Prism[Error, ErrorA] = GenPrism[Error, ErrorA]
        val detailsOptional = (errorAPrism andThen errorADetailsLens).modify(_ + "2")

        assertTrue(detailsOptional(ErrorA("message", "details")).asInstanceOf[ErrorA].details == expectedDetails)
        assertTrue(detailsOptional(ErrorB) == ErrorB)
      },
      test("quicklens: modify details to details2") {
        val expectedDetails = "details2"
        val setDetails = modify(_: Error)(_.when[ErrorA].details).using(_ + "2")
        assertTrue(setDetails(ErrorA("message", "details")).asInstanceOf[ErrorA].details == expectedDetails)
        assertTrue(setDetails(ErrorB) == ErrorB)
      }
    )
}
