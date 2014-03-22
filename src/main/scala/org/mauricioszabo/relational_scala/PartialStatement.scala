package relational

import java.sql._

case class PartialStatement(query: String, attributes: Seq[Any]) {
  lazy val toPseudoSQL = normalizedAttrs.foldLeft(query) { (query, attribute) =>
    query.replaceFirst("\\?", attribute)
  }

  private lazy val normalizedAttrs = attributes map normalize

  private def normalize(attribute: Any) = attribute match {
    case str: String => "'" + str.replaceAll("'", "''") + "'"
    //case date: Date => "'" + date.toString + "'"
    //case time: Time => "'" + time.utc + "'"
    case _ => attribute.toString
  }

  def createStatement(connection: Connection) = {
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
