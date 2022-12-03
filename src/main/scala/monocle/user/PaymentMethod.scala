package monocle.user

import java.time.YearMonth

sealed trait PaymentMethod
object PaymentMethod {
  case class PayPal(email: String) extends PaymentMethod

  case class DebitCard(
                        cardNumber: String,
                        expirationDate: YearMonth,
                        securityCode: Int
                      ) extends PaymentMethod
}