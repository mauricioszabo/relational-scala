package org.mauricioszabo.relational_scala.results

class Attribute(obj: Any) {
  def value = obj.toString

  def as(p: Int.type) = value.toInt
  def as(p: Double.type) = value.toDouble
  def as(p: Long.type) = value.toLong
  def as(p: Short.type) = value.toShort
}
