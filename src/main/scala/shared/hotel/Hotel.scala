package shared.hotel

case class Hotel(name: String, address: String, rating: Int, rooms: List[Room], facilities: Map[String, List[String]])
