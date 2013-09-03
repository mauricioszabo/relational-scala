package org.mauricioszabo.relational_scala.tables

import org.mauricioszabo.relational_scala._
import scala.language.dynamics

trait TableLike extends Partial with Dynamic {
  def as: Alias = as(Random.generate(5))
  def as(name: String): Alias

  def representation: String

  def selectDynamic(field: String) = apply(field)
  def apply(attribute: Symbol): attributes.Attribute = this(attribute.name)
  def apply(attribute: String): attributes.Attribute

  def * = new attributes.Attribute(this, "*")

  override def toString = getClass.getName + "("+representation+")"
}
