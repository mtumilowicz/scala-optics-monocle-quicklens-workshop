package workshop.optics

import monocle.Monocle.toAppliedFocusOps
import monocle.user._
import monocle.workshop.optics.Optics
import monocle.workshop.vanilla.VanillaOptics
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object OpticsSpec extends ZIOSpecDefault {

  val user = User("MT", Address(1, "1012"), PaymentMethod.PayPal("m@gmail.com"))

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
      }
    )
}
