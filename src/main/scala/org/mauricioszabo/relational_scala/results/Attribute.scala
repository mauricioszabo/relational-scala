package relational.results
import scala.language.implicitConversions

class Attribute(val obj: Any) {
  def value = obj.toString

  def isNull = obj == null

  def as(p: Int.type) = value.toInt
  def as(p: Float.type) = value.toFloat
  def as(p: Double.type) = value.toDouble
  def as(p: Long.type) = value.toLong
  def as(p: Short.type) = value.toShort

  override def equals(other: Any) = other match {
    case n: Attribute => obj == n.obj
    case _ => false
  }
}

object Attribute {
  def apply(attribute: Any) = attribute match {
    case n: Int => new NumericAttribute(n)
    case n: Double => new NumericAttribute(n)
    case n: Float => new NumericAttribute(n)
    case n: Long => new NumericAttribute(n)
    case n: Short => new NumericAttribute(n)
    case _ => new Attribute(attribute)
  }

  implicit def str(a: Attribute) = a.value
  implicit def int(a: Attribute): Int = a as Int
  implicit def float(a: Attribute): Float = a as Float
  implicit def double(a: Attribute): Double = a as Double
  implicit def long(a: Attribute): Long = a as Long
  implicit def short(a: Attribute): Short = a as Short
}
