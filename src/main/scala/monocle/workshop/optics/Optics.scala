package monocle.workshop.optics

import monocle.macros.GenLens
import monocle.user.{Address, PaymentMethod, User}

object Optics extends App {

  lazy val setStreetNo = GenLens[Address](_.streetNumber)
  lazy val setAddress = GenLens[User](_.address)

}
