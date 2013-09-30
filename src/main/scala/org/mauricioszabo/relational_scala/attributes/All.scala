package org.mauricioszabo.relational_scala.attributes

import org.mauricioszabo.relational_scala.PartialStatement

class All extends AttributeLike {
  lazy val partial = new PartialStatement("*", Nil)
}
