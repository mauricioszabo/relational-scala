package org.mauricioszabo.relational_scala.comparissions

import org.mauricioszabo.relational_scala.attributes._
import org.mauricioszabo.relational_scala.PartialStatement

class Equality(comparission: String, attribute: Attribute, other: Any) extends Comparission {
  lazy val partial = other match {
    case a: Attribute => new PartialStatement(query + a.representation, Nil)
    case null => handleNull
    case _ => new PartialStatement(query + "?", List(other))
  }

  private lazy val query = attribute.representation + " " + comparission + " "

  private def handleNull = comparission match {
    case "=" => new PartialStatement(attribute.representation + " IS NULL", Nil)
    case "<>" => new PartialStatement(attribute.representation + " IS NOT NULL", Nil)
    case _ => new PartialStatement(attribute.representation + " " + comparission + " NULL", Nil)
  }
}
