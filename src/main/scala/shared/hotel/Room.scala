package shared.hotel

case class Room(name: String, boardType: Option[String], price: Price, roomTariff: RoomTariff)
