package org.mauricioszabo.relational_scala.comparissions

import org.mauricioszabo.relational_scala.PartialStatement

object None extends Comparission {
  override protected def present = false

  lazy val partial = new PartialStatement("", Nil)

  override def ||(other: Comparission) = other
  override def &&(other: Comparission) = other
  override def unary_! = None
}
