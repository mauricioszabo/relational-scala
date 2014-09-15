package relational.queries.attr_helpers

import relational.queries.Query
import relational.clauses.Select
import relational.queries.Table
import relational.comparissions._
import relational.attributes.{Attribute => RAttr, AttributeLike}
import relational.tables.TableLike
import relational.joins._

class AttributeNormalizer[U]( allTables: List[TableLike], allJoins: List[Join], conditions: Comparission) {
  var from = allTables
  var joins = allJoins
  var where = normalizeComparissions(conditions)

  private def normalizeComparissions(c: Comparission): Comparission = c match {
    case Equality(_, a1: RAttr, a2: RAttr) =>
      extractJoins(a1.table, a2.table, c.asInstanceOf[Equality])
    case And(comparissions) =>
      comparissions.foldLeft(None: Comparission) { (previous, comp) =>
        previous && normalizeComparissions(comp)
      }
    case _ => c
  }

  private def extractJoins(t1: TableLike, t2: TableLike, comparission: Equality) = {
    val prev = from.toSet
    val currentTable = from.head
    if(t1 != t2 && prev.contains(t1) && prev.contains(t2)) {
      val toRemove = (Set(t1, t2) - currentTable).head
      from = from.filterNot(_ == toRemove)
      val join = new InnerJoin(toRemove, comparission)
      joins = join::joins.toList
      None
    } else {
      comparission
    }
  }
}
