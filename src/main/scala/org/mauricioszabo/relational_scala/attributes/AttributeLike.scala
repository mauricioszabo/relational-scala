package org.mauricioszabo.relational_scala.attributes

import org.mauricioszabo.relational_scala._

trait AttributeLike extends Partial {
  def as: Alias = as(Random.generate(5))
  def as(name: String): Alias = new Alias(name, partial.query, partial.attributes)

  def selectPartial = partial
  override def toString = getClass.getName + "(" + partial.query + ")"
}
