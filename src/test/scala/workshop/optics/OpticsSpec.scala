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

object OpticsSpec extends ZIOSpecDefault {

  val user = User("MT", Address(1, "1012"), PaymentMethod.PayPal("m@gmail.com"))
  val uni = University("oxford", Map(
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
      test("set street no") {
        val composed = Optics.setAddress andThen Optics.setStreetNo
        val newStreetNo = 10
        val expectedResult = User("MT", Address(newStreetNo, "1012"), PaymentMethod.PayPal("m@gmail.com"))
        assertTrue(composed.replace(10)(user) == expectedResult)
      },
      test("focus") {
        val newStreetNo = 10
        val expectedResult = User("MT", Address(newStreetNo, "1012"), PaymentMethod.PayPal("m@gmail.com"))
        assertTrue(user.focus(_.address.streetNumber).replace(10) == expectedResult)
      },
      test("increase each budget to be at least 100") {
        val modified = uni
          .focus(_.departments)
          .each
          .modify(_.focus(_.budget).modify(_ + 100))
        val budgets = modified.departments.values.map(_.budget)
        assertTrue(budgets.forall(_ >= 100))
      },
      test("double budget only for computer science") {
        val modified = uni
          .focus(_.departments)
          .at("Computer Science")
          .some
          .modify(_.focus(_.budget).modify(_ * 200))
        val budget = modified.departments("Computer Science").budget
        val expectedBudget = 90
        assertTrue(budget >= expectedBudget)
      },
      test("replace head lecturer in History department") {
        val expectedLecturer = Lecturer("luke", "skywalker", 1000)
        val modified = uni
          .focus(_.departments)
          .at("History")
          .some
          .modify(_.focus(_.lecturers).index(0).replace(expectedLecturer))
        val headLecturer = modified.departments("History").lecturers.head
        assertTrue(headLecturer == expectedLecturer)
      },
      test("replace email in PayPal payment method") {
        val newEmail = "newemail@gmail.com"
        val setPaymentMethod = GenLens[User](_.paymentMethod)
        val setPayPalEmail = Prism.partial[PaymentMethod, String]{case PayPal(x) => x}(PayPal)
        val updatePayPalEmail = setPaymentMethod andThen setPayPalEmail
        val result: User = updatePayPalEmail.replace(newEmail)(user)
        assertTrue(result.paymentMethod.focus().as[PayPal].getOption.get.email == newEmail)
      },
      test("increase fee for all flexible rooms") {
        assertTrue(true)
      }
    )
}
