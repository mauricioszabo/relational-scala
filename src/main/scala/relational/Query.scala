package relational

import scala.language.implicitConversions
import relational.clauses.Select

trait Query extends QueryBase[Selector with Query] {
  private def toSelector: Selector with Query = this match {
    case s: Selector with Query => s
    case _ => new Selector(
      select=new Select(false, table, new attributes.All()),
      from=List(table)
    ) with Query
  }

  protected def withSelector(fn: Selector => Selector) = {
    val selector = new Selector(fn(toSelector)) with Query
    selector.table = table
    selector
  }
}
