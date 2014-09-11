package relational.attributes

import relational.comparissions._

trait Comparable extends AttributeLike {
  def ===(other: => Any ) = this == other
  def ==(other: => Any ) = new Equality("=", this, Attribute.wrap(other))
  def ->(other: => Any ) = this == other

  def !=(other: => Any ) = new Equality("<>", this, Attribute.wrap(other))
  def <=(other: Any) = new Equality("<=", this, Attribute.wrap(other))
  def <(other: Any) = new Equality("<", this, Attribute.wrap(other))
  def >=(other: Any) = new Equality(">=", this, Attribute.wrap(other))
  def >(other: Any) = new Equality(">", this, Attribute.wrap(other))

  def isNull = new Function('null, this)

  def =~(other: Any) = like(other)
  def like(other: Any) = new Equality("LIKE", this, Attribute.wrap(other))
  def !~(other: Any) = notLike(other)
  def notLike(other: Any) = new Equality("NOT LIKE", this, Attribute.wrap(other))

  def in(list: Seq[Any]) = new In(this, list)

  def sum = new Function('sum, this)
  def avg = new Function('avg, this)
  def min = new Function('min, this)
  def max = new Function('max, this)
  def upper = new Function('upper, this)
  def lower = new Function('lower, this)
  def count = new Function('count, this)
  def length = new Function('length, this)
}
