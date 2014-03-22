package relational.comparissions

import relational.PartialStatement

object None extends Comparission {
  override protected def present = false

  lazy val partial = new PartialStatement("", Nil)

  override def ||(other: Comparission) = other
  override def &&(other: Comparission) = other
  override def unary_! = None
}
