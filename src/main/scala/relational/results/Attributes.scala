package relational.results

import scala.language.dynamics

class Attributes(val attribute: Map[Symbol, Attribute]) extends Dynamic {
  def selectDynamic[A](attr: String)(implicit ev: Attribute => A) = ev(attribute(Symbol(attr)))
  def get(name: Symbol) = attribute(name).value
}
