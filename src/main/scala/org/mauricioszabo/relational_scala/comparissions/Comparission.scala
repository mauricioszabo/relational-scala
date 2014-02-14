package org.mauricioszabo.relational_scala.comparissions

import org.mauricioszabo.relational_scala._
import org.mauricioszabo.relational_scala.comparissions._

trait Comparission extends attributes.Comparable {
  protected def present = true

  def unary_! : Comparission = new Not(this)

  def ||(other: Comparission): Comparission =
    if(other.present) new Or(List(this, other))
    else this

  def &&(other: Comparission): Comparission =
    if(other.present) new And(List(this, other))
    else this
}
