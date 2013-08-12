package org.mauricioszabo.relational_scala.orders

import org.mauricioszabo.relational_scala._

case class Descending(something: Partial) extends Partial {
  lazy val partial = {
    val partial = something.partial
    new PartialStatement("("+partial.query+") DESC", partial.attributes)
  }
}

