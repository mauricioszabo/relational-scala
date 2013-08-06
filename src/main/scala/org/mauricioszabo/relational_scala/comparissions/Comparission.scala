package org.mauricioszabo.relational_scala.comparissions

import org.mauricioszabo.relational_scala._
import org.mauricioszabo.relational_scala.comparissions._

trait Comparission {
  def partial: PartialStatement

  def unary_! = new Not(this)

  def ||(other: Comparission): Comparission = new Or(List(this, other))
  def &&(other: Comparission): Comparission = new And(List(this, other))
}

