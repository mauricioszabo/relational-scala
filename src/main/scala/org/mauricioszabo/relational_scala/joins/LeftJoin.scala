package org.mauricioszabo.relational_scala.joins

import org.mauricioszabo.relational_scala._

class LeftJoin(table: tables.TableLike, condition: comparissions.Comparission) extends Join {
  lazy val partial = newPartial(table, condition, "LEFT JOIN")
}
