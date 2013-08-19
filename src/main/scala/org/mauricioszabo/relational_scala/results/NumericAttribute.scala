package org.mauricioszabo.relational_scala.results

import scala.math.ScalaNumericAnyConversions

class NumericAttribute[A <% ScalaNumericAnyConversions](number: A) extends Attribute(number) {
  override def as(p: Int.type) = number.toInt
  override def as(p: Double.type) = number.toDouble
  override def as(p: Long.type) = number.toLong
  override def as(p: Short.type) = number.toShort
}
