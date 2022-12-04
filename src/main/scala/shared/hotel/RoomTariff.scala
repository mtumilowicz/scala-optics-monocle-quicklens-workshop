package shared.hotel

sealed trait RoomTariff
case class NonRefundable(fee: BigDecimal) extends RoomTariff
case class Flexible(fee: BigDecimal) extends RoomTariff