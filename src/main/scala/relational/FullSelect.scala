package relational

trait FullSelect extends Partial {
  def as(alias: String) = new tables.Alias(alias, "("+partial.query+")", partial.attributes)

  val connection: java.sql.Connection

  def resultSet = {
    val statement = partial.createStatement(connection)
    statement.executeQuery
  }
}
