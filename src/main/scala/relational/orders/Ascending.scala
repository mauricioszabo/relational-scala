package relational.orders

import relational._

case class Ascending(something: Partial) extends Ordering {
  lazy val partial = for( p <- something.partial ) yield "(" + p.query + ") ASC" -> p.params
}
