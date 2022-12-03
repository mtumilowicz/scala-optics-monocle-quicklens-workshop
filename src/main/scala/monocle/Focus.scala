package monocle

import monocle.Monocle.toAppliedFocusOps
import monocle.user.{Address, PaymentMethod, User}

object Focus extends App {

  val tUser = User("MT", Address(1, "1012"), PaymentMethod.PayPal("m@gmail.com"))

  println(tUser.focus(_.address.streetNumber).replace(10))

}
