package org.mauricioszabo.relational_scala.orm.config

object Connection {
  var getConnection: () => java.sql.Connection = { null }
}

