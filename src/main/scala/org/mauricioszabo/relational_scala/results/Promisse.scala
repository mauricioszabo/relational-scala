package org.mauricioszabo.relational_scala.results

class Promisse(resultSet: java.sql.ResultSet) extends Traversable[Attributes] {
  def foreach[U](fn: Attributes => U) {
    while(resultSet.next) fn(new Attributes(resultSet))
  }
}
