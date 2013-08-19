package org.mauricioszabo.relational_scala.tables

import org.mauricioszabo.relational_scala._
import org.mauricioszabo.relational_scala.attributes.Attribute
import org.mauricioszabo.relational_scala._

class Alias(name: String, query: String, attributes: Seq[Any]) extends TableLike {
  def as(name: String) = new Alias(name, query, attributes)
  lazy val representation = name
  lazy val partial = new PartialStatement(query + " " + name, attributes)
  def apply(attribute: String) = new Attribute(this, attribute)
}
