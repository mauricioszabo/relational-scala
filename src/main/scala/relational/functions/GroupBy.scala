package relational.functions

object Sum extends SqlAggregateFunction {
  val index = 0
  define('all -> "SUM($0)")
}

object Avg extends SqlAggregateFunction {
  val index = 0
  define('all -> "AVG($0)")
}

object Max extends SqlAggregateFunction {
  val index = 0
  define('all -> "MAX($0)")
}

object Min extends SqlAggregateFunction {
  val index = 0
  define('all -> "MIN($0)")
}

object Count extends SqlAggregateFunction {
  val index = 0
  define('all -> "COUNT($0)")
}

object CountDistinct extends SqlAggregateFunction {
  val index = 0
  define('all -> "COUNT(DISTINCT $0)")
}
