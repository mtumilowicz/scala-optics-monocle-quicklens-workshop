package monocle

import monocle.user._

object Xyz extends App {

  val setStreetNo = lib.Lens[Address, Int](_.streetNumber, f => ad => ad.copy(streetNumber = f(ad.streetNumber)))
  val setAddress = lib.Lens[User, Address](_.address, f => u => u.copy(address = f(u.address)))

  val tUser = User("MT", Address(1, "1012"), PaymentMethod.PayPal("m@gmail.com"))

  println((setAddress compose setStreetNo).set(10)(tUser))

}
