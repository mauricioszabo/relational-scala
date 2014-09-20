package relational.functions

object Sum extends SqlAggregateFunction[Double] {
  val index = 0
  define('all -> "SUM($0)")
}

object Avg extends SqlAggregateFunction[Double] {
  val index = 0
  define('all -> "AVG($0)")
}

object Max extends SqlAggregateFunction[Double] {
  val index = 0
  define('all -> "MAX($0)")
}

object Min extends SqlAggregateFunction[Double] {
  val index = 0
  define('all -> "MIN($0)")
}

object Count extends SqlAggregateFunction[Int] {
  val index = 0
  define('all -> "COUNT($0)")
}

object CountDistinct extends SqlAggregateFunction[Int] {
  val index = 0
  define('all -> "COUNT(DISTINCT $0)")
}
