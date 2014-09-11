package relational.tables

import relational._
import relational.attributes.Attribute
import relational._

class Alias(name: String, query: String, attributes: Seq[Any]) extends TableLike {
  def as(name: String) = new Alias(name, query, attributes)
  lazy val representation = name
  lazy val partial = new PartialStatement(query + " " + name, attributes)
  def apply(attribute: String) = new Attribute(this, attribute)
}