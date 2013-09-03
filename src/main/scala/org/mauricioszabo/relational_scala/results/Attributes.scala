package org.mauricioszabo.relational_scala.results

import scala.language.dynamics

class Attributes(columns: Seq[(Symbol, Attribute)]) extends Dynamic {
  def selectDynamic[A](attr: String)(implicit ev: Attribute => A) = ev(attribute(Symbol(attr)))

  val attribute = Map(columns: _*)
  def get(name: Symbol) = attribute(name).value
}
