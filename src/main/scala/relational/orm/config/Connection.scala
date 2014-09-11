package relational.orm.config

object Connection {
  var getConnection: () => java.sql.Connection = { null }
}

