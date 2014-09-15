package relational.comparissions

import relational.PartialStatement

object None extends Comparission {
  lazy val partial = new PartialStatement("", Nil)

  override def ||(other: Comparission) = other
  override def &&(other: Comparission) = other
  override def unary_! = None
}
