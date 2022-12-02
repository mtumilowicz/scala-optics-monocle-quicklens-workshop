package monocle

import monocle.Monocle.toAppliedFocusOps

import scala.collection.SortedMap
import monocle.syntax.all
import monocle.function.all
import monocle.std.map._
import monocle.syntax._
import monocle.Optional

case class Lecturer(firstName: String, lastName: String, salary: Int)
case class Department(budget: Int, lecturers: List[Lecturer])
case class University(name: String, departments: Map[String, Department])

object Abc extends App {

  val uni = University("oxford", Map(
    "Computer Science" -> Department(45, List(
      Lecturer("john", "doe", 10),
      Lecturer("robert", "johnson", 16)
    )),
    "History" -> Department(30, List(
      Lecturer("arnold", "stones", 20)
    ))
  ))

  val departments = Focus[University](_.departments)
  val a = departments.at("History").replace(None)(uni)

  val uni2 = uni.focus(_.departments).at("History").some.modify(_.focus(_.lecturers).replace(List(Lecturer("arnold1", "stones", 20))))

  println(uni2)

  val map = Map(1 -> Department(45, List(
    Lecturer("john", "doe", 10),
    Lecturer("robert", "johnson", 16)
  )))

  map.focus().at(1).some.replace(Department(46, List(
    Lecturer("john", "doe", 10),
    Lecturer("robert", "johnson", 16)
  )))

  map.focus().at(1).some.modify(_.focus(_.budget).modify(_ + 10))

}
