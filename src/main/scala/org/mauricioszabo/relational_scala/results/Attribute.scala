package org.mauricioszabo.relational_scala.results

class Attribute(obj: Any) {
  def value = obj.toString

  def as(p: Int.type) = obj.asInstanceOf[Int]
  def as(p: Double.type) = obj.asInstanceOf[Double]
  def as(p: Object) = obj.toString
}
