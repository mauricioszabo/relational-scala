package relational.orders

import relational._

case class Descending(something: Partial) extends Partial {
  lazy val partial = for( p <- something.partial ) yield "(" + p.query + ") DESC" -> p.params
}
