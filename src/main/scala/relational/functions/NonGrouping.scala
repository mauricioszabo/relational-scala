package relational.functions

object Length extends SqlFunction[Int] {
  define('all -> "LENGTH($0)")
}

object Upper extends SqlFunction[String] {
  define('all -> "UPPER($0)")
}

object Lower extends SqlFunction[String] {
  define('all -> "LOWER($0)")
}
