package relational.attributes

import relational.comparissions._
import relational.{PartialStatement, Partial, Adapter}

class Function(name: Symbol, self: Partial, params: Partial*)(implicit adapter: Adapter) extends Comparable {
  lazy val partial = {
    val function = adapter.getFunction(name)
    val(query, attributes) = function(self, params)
    new PartialStatement(query, attributes)
  }
}
