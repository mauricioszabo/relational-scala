package org.mauricioszabo.relational_scala.orm

class Validated[A] extends Property[A] {
  private var validOnlyIf = { a: Property[A] => true }

  def validIf(condition: Property[A] => Boolean) = {
    validOnlyIf = condition
    this
  }

  override protected def validate = validOnlyIf(this)
}
