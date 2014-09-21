package relational

import relational.orders._

trait Partial {
  def partial: PartialStatement

  def asc = new Ascending(this)
  def desc = new Descending(this)

  def equivalentTo(other: Partial) = {
    val adapter = new Adapter
    val PartialStatement(thisSQL, thisAttributes) = this.partial
    val PartialStatement(otherSQL, otherAttributes) = other.partial
    thisSQL(adapter) == otherSQL(adapter) && thisAttributes == otherAttributes
  }
}
