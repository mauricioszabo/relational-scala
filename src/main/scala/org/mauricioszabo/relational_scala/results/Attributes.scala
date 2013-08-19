package org.mauricioszabo.relational_scala.results

class Attributes(row: java.sql.ResultSet) {
  private val meta = row.getMetaData
  private val columns = 1.to(meta.getColumnCount).map { i =>
    val obj = row.getObject(i).asInstanceOf[Any]
    val attribute = obj match {
      case n: Int => new NumericAttribute(n)
      case n: Double => new NumericAttribute(n)
      case n: Long => new NumericAttribute(n)
      case n: Short => new NumericAttribute(n)
      case _ => new Attribute(obj)
    }
    Symbol(meta.getColumnName(i)) -> attribute
  }

  val attribute = Map(columns: _*)
  def get(name: Symbol) = attribute(name).value
}
