package workshop.optics

import monocle._
import monocle.macros.GenLens
import monocle.syntax.all._
import shared.hotel._
import shared.university._
import shared.user.PaymentMethod.PayPal
import shared.user.{Address, PaymentMethod, User}
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}
import com.softwaremill.quicklens._

object OpticsSpec extends ZIOSpecDefault {

  val user = User("MT", Address(1, "1012"), PaymentMethod.PayPal("m@gmail.com"))
  val university = University("oxford", Map(
    "Computer Science" -> Department(45, List(
      Lecturer("john", "doe", 10),
      Lecturer("robert", "johnson", 16)
    )),
    "History" -> Department(30, List(
      Lecturer("arnold", "stones", 20)
    ))
  ))
  val rooms = List(
    Room("Double", Some("Half Board"), Price(10, "USD"), NonRefundable(1)),
    Room("Twin", None, Price(20, "USD"), Flexible(0)),
    Room("Executive", None, Price(200, "USD"), Flexible(0))
  )
  val facilities = Map("business" -> List("conference room"))
  val hotel = Hotel("Hotel Paradise", "100 High Street", 5, rooms, facilities)

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("OpticsSpec")(
      test("monocle: user - set street number to 10; use GenLens") {
        val setStreetNumber = GenLens[Address](_.streetNumber)
        val setAddress = GenLens[User](_.address)
        val composed = setAddress andThen setStreetNumber
        val newStreetNo = 10
        val expectedResult = User("MT", Address(newStreetNo, "1012"), PaymentMethod.PayPal("m@gmail.com"))
        assertTrue(composed.replace(10)(user) == expectedResult)
      },
      test("monocle: user - set street number to 10; use: focus") {
        val newStreetNo = 10
        val expectedResult = User("MT", Address(newStreetNo, "1012"), PaymentMethod.PayPal("m@gmail.com"))
        assertTrue(user.focus(_.address.streetNumber).replace(10) == expectedResult)
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
          .andThen(Prism.partial[PaymentMethod, String]{case PayPal(x) => x}(PayPal))
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
      }
    )
}
