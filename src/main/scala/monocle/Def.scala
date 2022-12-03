package monocle

import monocle.macros.GenLens
import monocle.user.{Address, PaymentMethod, User}

object Def extends App {

  val setStreetNo = GenLens[Address](_.streetNumber)
  val setAddress = GenLens[User](_.address)

  val tUser = User("MT", Address(1, "1012"), PaymentMethod.PayPal("m@gmail.com"))

  println((setAddress andThen setStreetNo).replace(10)(tUser))

}
