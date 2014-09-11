package relational.joins

import relational._

class LeftJoin(table: tables.TableLike, condition: comparissions.Comparission) extends Join {
  lazy val partial = newPartial(table, condition, "LEFT JOIN")
}
