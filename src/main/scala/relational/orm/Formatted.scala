package relational.orm

import scala.util.matching.Regex

class Formatted[A] extends Property[A] {
  private var format = "".r

  def withFormat(format: Regex) = {
    this.format = format
    this
  }

  override protected def validate = optionValue match {
    case Some(value) => format.findFirstIn(value.toString) != None
    case None => false
  }
}
