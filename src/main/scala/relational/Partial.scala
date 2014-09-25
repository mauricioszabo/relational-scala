package relational

import relational.orders._

trait Partial {
  def partial: PartialStatement

  def asc = new Ascending(this)
  def desc = new Descending(this)

  def equivalentTo(other: Partial) = {
    val adapter = new Adapter
    val (thisQuery, thisAttrs) = this.partial.tuple(adapter)
    val (otherQuery, otherAttrs) = other.partial.tuple(adapter)
    thisQuery == otherQuery && thisAttrs == otherAttrs
  }
}
