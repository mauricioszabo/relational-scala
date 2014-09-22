package relational.tables

import relational._
import relational.attributes.Escape

case class Table(private val name: String) extends TableLike {
  lazy val partial = new PartialStatement(Nil)(a => representation(a))
  def representation(a: Adapter) = Escape(a, name)
  def apply(attribute: String) = new attributes.Attribute(this, attribute)
  def as(name: String) = new Alias(name, this)
}
