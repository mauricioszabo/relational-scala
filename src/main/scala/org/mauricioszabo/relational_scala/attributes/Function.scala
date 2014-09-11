package relational.attributes

import relational.comparissions._
import relational.{PartialStatement, Partial, Adapter}

class Function(name: Symbol, self: Partial, params: Partial*) extends Comparable {
  lazy val partial = {
    val function = Adapter.getFunction(name)
    val(query, attributes) = function(self, params)
    new PartialStatement(query, attributes)
  }
}
