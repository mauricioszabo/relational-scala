package org.mauricioszabo.relational_scala.joins

import org.mauricioszabo.relational_scala.tables.TableLike
import org.mauricioszabo.relational_scala._

class JoinHelper[A](query: QueryBase[A], otherTable: TableLike, joinKind: Symbol) {
  def on(fn: (TableLike, TableLike) => comparissions.Comparission): A = {
    val comparission = fn(query.table, otherTable)
    val join = createJoin(comparission)
    val joins = concatenateJoin(join)
    query.withSelector { s => s.copy(join=joins) }
  }

  private def createJoin(comparission: comparissions.Comparission) = joinKind match {
    case 'inner => new InnerJoin(otherTable, comparission)
    case 'left => new LeftJoin(otherTable, comparission)
    case 'right => new RightJoin(otherTable, comparission)
  }

  private def concatenateJoin(join: joins.Join) = query match {
    case s: Selector => s.join :+ join
    case _ => Seq(join)
  }
}
