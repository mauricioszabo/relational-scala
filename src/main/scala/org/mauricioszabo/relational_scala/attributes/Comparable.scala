package org.mauricioszabo.relational_scala.attributes

import org.mauricioszabo.relational_scala.comparissions._

trait Comparable extends AttributeLike {
  def ==(other: => Any ) = new Equality("=", this, other)
  def ===(other: => Any ) = this == other
  def ->(other: => Any ) = this == other

  def !=(other: => Any ) = new Equality("<>", this, other)
  def <=(other: Any) = new Equality("<=", this, other)
  def <(other: Any) = new Equality("<", this, other)
  def >=(other: Any) = new Equality(">=", this, other)
  def >(other: Any) = new Equality(">", this, other)

  def isNull = this == null

  def =~(other: Any) = like(other)
  def like(other: Any) = new Equality("LIKE", this, other)
  def !~(other: Any) = notLike(other)
  def notLike(other: Any) = new Equality("NOT LIKE", this, other)

  def in(list: Seq[Any]) = new In(this, list)

  def sum = new Function("SUM", this)
  def avg = new Function("AVG", this)
  def min = new Function("MIN", this)
  def max = new Function("MAX", this)
  def upper = new Function("UPPER", this)
  def lower = new Function("LOWER", this)
  def length = new Function("LENGTH", this)
  def count = new Function("COUNT", this)
}
