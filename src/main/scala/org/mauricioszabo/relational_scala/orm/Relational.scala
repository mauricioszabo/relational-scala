package org.mauricioszabo.relational_scala.orm

import org.mauricioszabo.relational_scala.results.{Attribute => RAttribute}
import org.mauricioszabo.relational_scala.results.Attributes
import scala.language.implicitConversions

class Relational(mapping: Seq[(Symbol, Any)]) extends Attributes(
    mapping.map { case(key, value) => (key, RAttribute(value)) }
  ) {

  def attr(key: Symbol) = new Attribute( attribute get key )
}
