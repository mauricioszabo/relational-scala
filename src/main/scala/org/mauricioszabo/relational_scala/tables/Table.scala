package org.mauricioszabo.relational_scala.tables

import org.mauricioszabo.relational_scala._

class Table(name: String) extends TableLike {
  def representation = name

  def apply(attribute: Symbol) = new attributes.Table(this, attribute.name)
  def apply(attribute: String) = new attributes.Table(this, attribute)
  def * = new attributes.Table(this, "*")
  def all = *
}
