package org.mauricioszabo.relational_scala.results

class Attributes(row: java.sql.ResultSet) {
  private val meta = row.getMetaData
  private val columns = 1.to(meta.getColumnCount).map { i =>
    Symbol(meta.getColumnName(i)) -> new Attribute(row.getObject(i))
  }

  val attribute = Map(columns: _*)
  def get(name: Symbol) = attribute(name).value
}
