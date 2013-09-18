package org.mauricioszabo.relational_scala

import scala.language.implicitConversions

trait Query extends QueryBase {
  def all: Selector with Query = this match {
    case s: Selector with Query => s
    case _ => new Selector(select=List(table.*), from=List(table)) with Query
  }
}
