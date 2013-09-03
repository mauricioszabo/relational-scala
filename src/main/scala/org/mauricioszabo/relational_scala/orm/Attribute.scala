package org.mauricioszabo.relational_scala.orm

import scala.language.implicitConversions
import org.mauricioszabo.relational_scala.results.{Attribute => Attr}

class Attribute(protected val maybeAttr: Option[Attr])

object Attribute {
  implicit def attr2property[A](attribute: Attribute)(implicit ev: Attr => A): Property[A] = try {
    attribute.maybeAttr match {
      case Some(value) if(value.isNull) => new Property(None)
      case Some(value) => new Property(Some(ev(value)))
      case None => new Property(None)
    }
  } catch {
    case _: NumberFormatException => new Property(None)
  }
}
