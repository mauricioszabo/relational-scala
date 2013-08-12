package org.mauricioszabo.relational_scala.orders

import org.mauricioszabo.relational_scala._

case class Ascending(something: Partial) extends Partial {
  lazy val partial = {
    val partial = something.partial
    new PartialStatement("("+partial.query+") ASC", partial.attributes)
  }
}
