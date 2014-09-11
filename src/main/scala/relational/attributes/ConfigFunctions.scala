package relational.attributes

object ConfigFunctions {
  import relational.Adapter._

  defineFunctionN('null, 'all -> "$0 IS NULL")
  defineFunctionN('sum, 'all -> "SUM($0)")
  defineFunctionN('avg, 'all -> "AVG($0)")
  defineFunctionN('min, 'all -> "MIN($0)")
  defineFunctionN('max, 'all -> "MAX($0)")
  defineFunctionN('upper, 'all -> "UPPER($0)")
  defineFunctionN('lower, 'all -> "LOWER($0)")
  defineFunctionN('length, 'all -> "LENGTH($0)")
  defineFunctionN('count, 'all -> "COUNT($0)")
}
