package org.mauricioszabo.relational_scala.results

class Promisse(resultSet: java.sql.ResultSet) extends Traversable[Attributes] {
  def foreach[U](fn: Attributes => U) = while(resultSet.next) {
    val meta = resultSet.getMetaData
    val columns = 1.to(meta.getColumnCount).map { i =>
      val obj = resultSet.getObject(i).asInstanceOf[Any]
      Symbol(meta.getColumnName(i)) -> Attribute(obj)
    }
    fn(new Attributes(Map(columns: _*)))
  }
}
