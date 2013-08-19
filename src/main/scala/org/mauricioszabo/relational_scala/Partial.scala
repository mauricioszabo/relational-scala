package org.mauricioszabo.relational_scala

import org.mauricioszabo.relational_scala.orders._

trait Partial {
  def partial: PartialStatement

  def asc = new Ascending(this)
  def desc = new Descending(this)
}
