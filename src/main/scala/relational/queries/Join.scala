package relational.queries

import relational.comparissions.Comparission
import relational.tables.{Table => RTable}

class LeftJoin protected(table: RTable, on: Comparission) {
}

object LeftJoin {
  def apply(tableName: Symbol, fn: RTable => Comparission) = {
    val table = new RTable(tableName.name)
    new LeftJoin(table, fn(table))
  }
}
