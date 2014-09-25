package relational.functions

object Length extends SqlFunction[Int] {
  define { case 'all => "LENGTH($0)" }
}

object Upper extends SqlFunction[String] {
  define { case 'all => "UPPER($0)" }
}

object Lower extends SqlFunction[String] {
  define { case 'all => "LOWER($0)" }
}
