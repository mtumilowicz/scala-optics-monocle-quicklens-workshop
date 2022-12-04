package workshop.optics

import monocle.macros.GenLens
import shared.user.{Address, User}

object Optics extends App {

  lazy val setStreetNo = GenLens[Address](_.streetNumber)
  lazy val setAddress = GenLens[User](_.address)

}
