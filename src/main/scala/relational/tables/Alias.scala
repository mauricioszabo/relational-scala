package relational.tables

import relational._
import relational.attributes.{Attribute, Escape}
import relational._

case class Alias(private val name: String,
                 private val query: String,
                 private val attributes: Seq[Any]) extends TableLike {
  def as(name: String) = new Alias(name, query, attributes)
  lazy val representation = Escape(name)
  lazy val partial = new PartialStatement(query + " " + name, attributes)
  def apply(attribute: String) = new Attribute(this, attribute)
}
