package org.mauricioszabo.relational_scala.orm

class Property[A](val possibleValue: Option[A]) {
  private var default: A = _

  def isPresent = possibleValue != None

  def value = possibleValue match {
    case Some(value) => value
    case None => default
  }
}
