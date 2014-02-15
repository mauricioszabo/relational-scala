package org.mauricioszabo.relational_scala.orm.mapper

import org.mauricioszabo.relational_scala._

// Simple trait to comply with the Selector's API.
trait SelectorCompliance {
  val select: clauses.Select
  val from: Seq[tables.TableLike]
  val where: comparissions.Comparission
  val group: Seq[attributes.AttributeLike]
  val having: comparissions.Comparission
  val join: Seq[joins.Join]
  val order: Seq[Partial]
  val connection: java.sql.Connection
  val limit: Int
  val offset: Int
}
