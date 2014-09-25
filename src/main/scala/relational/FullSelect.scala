package relational

trait FullSelect extends Partial {
  def as(alias: String) = new tables.Alias(alias, this)

  val connection: java.sql.Connection

  def resultSet = {
    val statement = partial.createStatement(connection)
    statement.executeQuery
  }
}
