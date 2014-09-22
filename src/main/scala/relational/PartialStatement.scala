package relational

import java.sql._

case class PartialStatementParams(query: String, params: Seq[Any], adapter: Adapter)

case class PartialStatement(tuple: Adapter => (String, Seq[Any])) {
  def toPseudoSQL(implicit adapter: Adapter) = {
    val (query, params) = tuple(adapter)
    val normalized = params map normalize
    normalized.foldLeft(query) { (query, attribute) =>
      query.replaceFirst("\\?", attribute)
    }
  }

  private def normalize(attribute: Any) = attribute match {
    case str: String => "'" + str.replaceAll("'", "''") + "'"
    //case date: Date => "'" + date.toString + "'"
    //case time: Time => "'" + time.utc + "'"
    case null => "NULL"
    case _ => attribute.toString
  }

  //FIXME: remove this code, it doesn't belongs here.
  def createStatement(connection: Connection) = {
    val (query, params) = tuple(null)
    val statement = connection.prepareStatement(query)
    setParams(statement, params)
    statement
  }

  private def setParams(statement: PreparedStatement, attributes: Seq[Any]) = 1 to attributes.size foreach { i =>
    attributes(i-1) match {
      case int: Int => statement.setObject(i, int)
      case str: String => statement.setObject(i, str)
    }
  }

  //Monadic operations.
  protected[relational] def map(fn: PartialStatementParams => (String, Seq[Any])): PartialStatement = {
    PartialStatement { adapter =>
      val (query, params) = tuple(adapter)
      fn(PartialStatementParams(query, params, adapter))
    }
  }

  protected[relational] def flatMap(fn: PartialStatementParams => PartialStatement): PartialStatement = {
    PartialStatement { adapter =>
      val (query, params) = tuple(adapter)
      fn(PartialStatementParams(query, params, adapter)).tuple(adapter)
    }
  }
}
