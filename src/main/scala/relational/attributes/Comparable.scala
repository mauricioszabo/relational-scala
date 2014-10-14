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
  def notIn(list: Seq[Any]): Comparission = new NotIn(this, list)

  def isNull: Comparission = new IsNull(this)
  def notNull: Comparission = new NotNull(this)
}
