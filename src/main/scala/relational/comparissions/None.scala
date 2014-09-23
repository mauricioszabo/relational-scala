package relational.comparissions

import relational.PartialStatement

object None extends Comparission {
  lazy val partial = PartialStatement { a => "" -> Nil }

  override protected def newEquality(k: String, a: Any): Comparission = None
  override def in(s: Seq[Any]) = None
  override def isNull = None

  override def ||(other: Comparission) = other
  override def &&(other: Comparission) = other
  override def unary_! = None
}
