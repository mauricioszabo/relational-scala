package org.mauricioszabo.relational_scala.attributes

import org.mauricioszabo.relational_scala._

class Alias(name: String, query: String, attributes: Seq[Any]) extends AttributeLike {
  override def as(name: String) = new Alias(name, query, attributes)
  lazy val partial = new PartialStatement(name, attributes)
  override lazy val selectPartial = new PartialStatement(query + " " + name, attributes)
}
