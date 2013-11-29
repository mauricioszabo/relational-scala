package org.mauricioszabo.relational_scala.comparissions

import org.mauricioszabo.relational_scala.attributes._
import org.mauricioszabo.relational_scala.PartialStatement

class Equality(
  protected val comparission: String,
  protected val attribute: AttributeLike,
  protected val other: Any) extends Comparission {

  lazy val partial = other match {
    case a: AttributeLike => new PartialStatement(query + a.partial.query, ap.attributes ++ a.partial.attributes)
    case null => handleNull
    case _ => new PartialStatement(query + "?", List(other) ++ ap.attributes)
  }

  private def handleNull = comparission match {
    case "=" => new PartialStatement(ap.query + " IS NULL", ap.attributes)
    case "<>" => new PartialStatement(ap.query + " IS NOT NULL", ap.attributes)
    case _ => new PartialStatement(ap.query + " " + comparission + " NULL", ap.attributes)
  }

  private lazy val query = ap.query + " " + comparission + " "
  private lazy val ap = attribute.partial
}
