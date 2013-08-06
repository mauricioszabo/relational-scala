package org.mauricioszabo.relational_scala

import org.mauricioszabo.relational_scala._

class Selector protected(clauses: Map[Symbol, Any]) {
  def this() = this(Map())

  def from(table: tables.TableLike*) = {
    val f = froms ++ table.toList
    new Selector(clauses ++ Map('from -> f))
  }

  private def froms = clauses('from).asInstanceOf[List[tables.TableLike]]
}
