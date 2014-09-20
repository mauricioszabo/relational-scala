package relational.tables

import relational._
import relational.attributes.Escape

case class Table(private val name: String)(implicit adapter: Adapter) extends TableLike {
  lazy val partial = new PartialStatement(representation, Nil)
  def representation = Escape(name)
  def apply(attribute: String) = new attributes.Attribute(this, attribute)
  def as(name: String) = new Alias(name, this)
}
