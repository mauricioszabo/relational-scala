package org.mauricioszabo.relational_scala.attributes

import org.mauricioszabo.relational_scala.comparissions._

class Function(function: String, attribute: Attribute) extends Attribute {
  lazy val representation = function + "(" + attribute.representation + ")"
}
