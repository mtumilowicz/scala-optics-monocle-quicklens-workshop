package workshop.vanilla

import monocle.user._
import monocle.workshop.vanilla.VanillaOptics
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assert, assertTrue}

object VanillaOpticsSpec extends ZIOSpecDefault {

  val user = User("MT", Address(1, "1012"), PaymentMethod.PayPal("m@gmail.com"))

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("VanillaOptics")(
      test("set street no") {
        val composed = VanillaOptics.setAddress compose VanillaOptics.setStreetNo
        val newStreetNo = 10
        val expectedResult = User("MT", Address(newStreetNo, "1012"), PaymentMethod.PayPal("m@gmail.com"))
        assertTrue(composed.set(10)(user) == expectedResult)
      }
    )
}