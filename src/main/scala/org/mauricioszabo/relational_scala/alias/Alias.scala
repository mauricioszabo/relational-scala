package org.mauricioszabo.relational_scala.alias

import org.mauricioszabo.relational_scala._

//trait Alias { self: Partial =>
//  private val p = self.partial
//
//  def as(name: String) = self match {
//    case table: tables.Table => new tables.Alias(name, p.query, p.attributes)
//    case alias: tables.Alias => subTableAlias(name, alias)
//  }
//
//  private def subTableAlias(name:String, alias: tables.Alias) = {
//    val (query, attributes) = alias.subAlias
//    new tables.Alias(name, query, attributes)
//  }
//}
