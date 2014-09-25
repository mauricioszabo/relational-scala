package relational.comparissions

import relational._
import relational.comparissions._

trait Comparission extends attributes.Comparable {
  def unary_! : Comparission = new Not(this)

  def ||(other: Comparission): Comparission = (this, other) match {
    case (_, None) => this
    case (Or(a1), Or(a2)) => Or(a1 ++ a2)
    case (_, Or(attributes)) => Or(this +: attributes)
    case (Or(attributes), _) => Or(attributes :+ other)
    case _ => new Or(Vector(this, other))
  }

  def &&(other: Comparission): Comparission = (this, other) match {
    case (_, None) => this
    case (And(a1), And(a2)) => And(a1 ++ a2)
    case (_, And(attributes)) => And(this +: attributes)
    case (And(attributes), _) => And(attributes :+ other)
    case _ => new And(Vector(this, other))
  }
}
