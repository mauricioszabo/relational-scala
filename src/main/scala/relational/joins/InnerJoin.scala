package relational.joins

import relational._

class InnerJoin(table: tables.TableLike, condition: comparissions.Comparission) extends Join {
  lazy val partial = newPartial(table, condition, "INNER JOIN")
}
