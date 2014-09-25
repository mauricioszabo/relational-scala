package relational.joins

import relational.tables.TableLike
import relational._

class JoinHelper[A](query: QueryBase[A], otherTable: TableLike, joinKind: Symbol) {
  def on(fn: (TableLike, TableLike) => comparissions.Comparission): A = {
    val comparission = fn(query.table, otherTable)
    val join = createJoin(comparission)
    val joins = concatenateJoin(join)
    query.join(joins)
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
