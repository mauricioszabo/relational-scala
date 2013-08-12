package org.mauricioszabo.relational_scala.tables

import org.mauricioszabo.relational_scala._

trait TableLike extends Partial {
  def representation: String

  def apply(attribute: Symbol) = new attributes.Attribute(this, attribute.name)
  def apply(attribute: String): attributes.Attribute

  def * = new attributes.Attribute(this, "*")

  override def toString = getClass.getName + "("+representation+")"
}
