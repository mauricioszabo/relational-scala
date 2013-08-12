package org.mauricioszabo.relational_scala.tables

import org.mauricioszabo.relational_scala._

class Table(name: String) extends TableLike {
  lazy val partial = new PartialStatement(representation, Nil)
  def representation = name

  def apply(attribute: String) = new attributes.Attribute(this, attribute)
}
