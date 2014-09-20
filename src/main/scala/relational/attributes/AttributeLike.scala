package relational.attributes

import relational._

trait AttributeLike extends Partial {
  def as: Alias = as(Random.generate(5))
  def as(name: String): Alias = new Alias(name, this)

  def selectPartial = partial
  override def toString = getClass.getName + "(" + partial.toPseudoSQL + ")"
}
