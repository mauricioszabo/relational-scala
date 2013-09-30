package org.mauricioszabo.relational_scala

import scala.language.implicitConversions
import org.mauricioszabo.relational_scala.clauses.Select

trait Query extends QueryBase[Selector with Query] {
  private def toSelector: Selector with Query = this match {
    case s: Selector with Query => s
    case _ => new Selector(
      select=new Select(false, table, new attributes.All()),
      from=List(table)
    ) with Query
  }

  protected[relational_scala] def withSelector(fn: Selector => Selector) = {
    val selector = new Selector(fn(toSelector)) with Query
    selector.table = table
    selector
  }
}
