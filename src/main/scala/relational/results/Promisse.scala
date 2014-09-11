package relational.results

class Promisse(resultSet: java.sql.ResultSet) extends Traversable[Attributes] {
  def foreach[U](fn: Attributes => U) = while(resultSet.next) {
    val meta = resultSet.getMetaData
    val columns = 1.to(meta.getColumnCount).foldLeft(Map[Symbol, Attribute]()) { (map, i) =>
      val columnName = Symbol(meta getColumnName i)
      map get columnName match {
        case Some(_) => map
        case None => map + (columnName -> Attribute(resultSet.getObject(i).asInstanceOf[Any]))
      }
    }
    fn(new Attributes(columns))
  }
}
