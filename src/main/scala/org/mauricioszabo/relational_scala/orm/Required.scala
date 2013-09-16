package org.mauricioszabo.relational_scala.orm

class Required[A] extends Property[A] {
  override protected def validate = optionValue match {
    case Some(value) => !value.toString.isEmpty
    case None => false
  }
}
