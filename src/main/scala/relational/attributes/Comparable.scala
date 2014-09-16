package relational.attributes

import relational.comparissions._
import relational.comparissions.None

trait Comparable extends AttributeLike {
  def ===(other: => Any ) = this == other
  def ->(other: => Any ) = this == other
  def ==(other: => Any ) = newEquality("=", other)

  def !=(other: => Any ) = newEquality("<>", other)
  def <=(other: Any) = newEquality("<=", other)
  def <(other: Any) = newEquality("<", other)
  def >=(other: Any) = newEquality(">=", other)
  def >(other: Any) = newEquality(">", other)

  def =~(other: Any) = like(other)
  def like(other: Any) = newEquality("LIKE", other)
  def !~(other: Any) = notLike(other)
  def notLike(other: Any) = newEquality("NOT LIKE", other)

  protected def newEquality(kind: String, other: Any): Comparission = other match {
    case None => None
    case _ => new Equality(kind, this, Attribute.wrap(other))
  }

  def in(list: Seq[Any]): Comparission = new In(this, list)

  def isNull: Comparission = new IsNull(this)

  def sum = new Function('sum, this)
  def avg = new Function('avg, this)
  def min = new Function('min, this)
  def max = new Function('max, this)
  def upper = new Function('upper, this)
  def lower = new Function('lower, this)
  def count = new Function('count, this)
  def length = new Function('length, this)
}
