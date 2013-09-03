package org.mauricioszabo.relational_scala.results

import scala.math.ScalaNumericAnyConversions

class NumericAttribute[A <% ScalaNumericAnyConversions](protected val number: A) extends Attribute(number) {
  override def as(p: Int.type) = number.toInt
  override def as(p: Float.type) = number.toFloat
  override def as(p: Double.type) = number.toDouble
  override def as(p: Long.type) = number.toLong
  override def as(p: Short.type) = number.toShort

  override def equals(other: Any) = other match {
    case n: NumericAttribute[A] => number == n.number
    case _ => false
  }
}
