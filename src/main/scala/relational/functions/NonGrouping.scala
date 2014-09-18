package relational.functions

object Length extends SqlFunction {
  define('all -> "LENGTH($0)")
}

object Upper extends SqlFunction {
  define('all -> "UPPER($0)")
}

object Lower extends SqlFunction {
  define('all -> "LOWER($0)")
}
