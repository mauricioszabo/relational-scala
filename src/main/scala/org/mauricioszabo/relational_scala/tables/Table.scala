package relational.tables

import relational._

class Table(name: String) extends TableLike {
  lazy val partial = new PartialStatement(representation, Nil)
  def representation = name
  def apply(attribute: String) = new attributes.Attribute(this, attribute)
  def as(name: String) = new Alias(name, partial.query, partial.attributes)
}
