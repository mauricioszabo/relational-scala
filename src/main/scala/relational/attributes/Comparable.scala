package relational.attributes

import relational.comparissions._
import relational.comparissions.None

trait Comparable extends AttributeLike {
  def ===(other: => Any ) = this == other
  def ->(other: => Any ) = this == other
  def ==(other: => Any ) = newEquality(Equality.Equals, other)

  def !=(other: => Any ) = newEquality(Equality.Diferent, other)
  def <=(other: Any) = newEquality(Equality.LtE, other)
  def <(other: Any) = newEquality(Equality.Lt, other)
  def >=(other: Any) = newEquality(Equality.GtE, other)
  def >(other: Any) = newEquality(Equality.Gt, other)

  def =~(other: Any) = like(other)
  def like(other: Any) = newEquality(Equality.Like, other)
  def !~(other: Any) = notLike(other)
  def notLike(other: Any) = newEquality(Equality.NotLike, other)

  protected def newEquality(kind: Equality.Comparission, other: Any): Comparission = other match {
    case None => None
    case _ => new Equality(kind, this, Attribute.wrap(other))
  }

  def in(list: Seq[Any]): Comparission = new In(this, list)
  def notIn(list: Seq[Any]): Comparission = new NotIn(this, list)

  def isNull: Comparission = new IsNull(this)
  def notNull: Comparission = new NotNull(this)
}
