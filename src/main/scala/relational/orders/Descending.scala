package relational.orders

import relational._

case class Descending(something: Partial) extends Partial {
  lazy val partial = {
    val partial = something.partial
    new PartialStatement("("+partial.query+") DESC", partial.attributes)
  }
}

