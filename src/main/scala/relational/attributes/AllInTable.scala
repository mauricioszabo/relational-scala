package relational.attributes

import relational.PartialStatement
import relational._

case class AllInTable(table: tables.TableLike) extends AttributeLike {
  lazy val partial = PartialStatement { a =>
    table.representation(a) + ".*" -> Nil
  }
}

