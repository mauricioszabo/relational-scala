package relational.functions

object Sum extends SqlAggregateFunction[Double] {
  val index = 0
  define { case 'all => "SUM($0)" }
}

object Avg extends SqlAggregateFunction[Double] {
  val index = 0
  define { case 'all => "AVG($0)" }
}

object Max extends SqlAggregateFunction[Double] {
  val index = 0
  define { case 'all => "MAX($0)" }
}

object Min extends SqlAggregateFunction[Double] {
  val index = 0
  define { case 'all => "MIN($0)" }
}

object Count extends SqlAggregateFunction[Int] {
  val index = 0
  define { case 'all => "COUNT($0)" }
}

object CountDistinct extends SqlAggregateFunction[Int] {
  val index = 0
  define { case 'all => "COUNT(DISTINCT $0)" }
}

object GroupConcat extends SqlAggregateFunction[String] {
  val index = 0

  define {
    case 'sqlite3 => "GROUP_CONCAT($0, $1)"
    case 'mysql => "GROUP_CONCAT($0 SEPARATOR $1)"
    case 'postgresql => "ARRAY_TO_STRING(ARRAY_AGG($0), $1)"
    case 'oracle => "LISTAGG($0, $1) WITHIN GROUP($0)"
  }
}
