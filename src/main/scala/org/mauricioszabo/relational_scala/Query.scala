package org.mauricioszabo.relational_scala

import scala.language.implicitConversions

trait Query extends QueryBase[Selector with Query] {
  private def toSelector: Selector with Query = this match {
    case s: Selector with Query => s
    case _ => new Selector(select=List(table.*), from=List(table)) with Query
  }

  protected[relational_scala] def withSelector(fn: Selector => Selector) = {
    val selector = new Selector(fn(toSelector)) with Query
    selector.table = table
    selector
  }
}
