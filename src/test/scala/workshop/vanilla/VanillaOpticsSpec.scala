package workshop.vanilla

import shared.lib
import shared.lib.Lens
import shared.user.{Address, PaymentMethod, User}
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

object VanillaOpticsSpec extends ZIOSpecDefault {

  val user = User("MT", Address(1, "1012"), PaymentMethod.PayPal("m@gmail.com"))

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("VanillaOptics")(
      test("set street no") {
        val setStreetNo = Lens[Address, Int](_.streetNumber, f => ad => ad.copy(streetNumber = f(ad.streetNumber)))
        val setAddress = lib.Lens[User, Address](_.address, f => u => u.copy(address = f(u.address)))
        val composed = setAddress compose setStreetNo
        val newStreetNo = 10
        val expectedResult = User("MT", Address(newStreetNo, "1012"), PaymentMethod.PayPal("m@gmail.com"))
        assertTrue(composed.set(10)(user) == expectedResult)
      }
    )
}
