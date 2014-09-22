package relational

import java.sql._

case class PartialStatement(sql: Adapter => String, attributes: Seq[Any]) {
  def this(attributes: Seq[Any])(sql: Adapter => String) = this(sql, attributes)
  def this(query: String, attributes: Seq[Any]) = this(attributes)(a => query)

  def toPseudoSQL(implicit adapter: Adapter) = {
    val query = sql(adapter)
    normalizedAttrs.foldLeft(query) { (query, attribute) =>
      query.replaceFirst("\\?", attribute)
    }
  }

  private lazy val normalizedAttrs = attributes map normalize

  private def normalize(attribute: Any) = attribute match {
    case str: String => "'" + str.replaceAll("'", "''") + "'"
    //case date: Date => "'" + date.toString + "'"
    //case time: Time => "'" + time.utc + "'"
    case null => "NULL"
    case _ => attribute.toString
  }

  def createStatement(connection: Connection) = {
    val query = sql(null)
    val statement = connection.prepareStatement(query)
    setParams(statement)
    statement
  }

  private def setParams(statement: PreparedStatement) = 1 to attributes.size foreach { i =>
    attributes(i-1) match {
      case int: Int => statement.setObject(i, int)
      case str: String => statement.setObject(i, str)
    }
  }

}
