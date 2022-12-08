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
import com.softwaremill.quicklens._

object Data {

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

  sealed trait Error

  case class ErrorA(message: String, details: String) extends Error

  case object ErrorB extends Error

}
