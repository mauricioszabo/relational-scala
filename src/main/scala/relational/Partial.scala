package relational

import relational.orders._

trait Partial {
  def partial: PartialStatement

  def asc = new Ascending(this)
  def desc = new Descending(this)

  def appendPartial(partials: Partial*) = {
    val PartialStatement(query, attributes) = this.partial
    val (q, a) = partials.view.map(_.partial).foldLeft(query -> attributes) {
      case((query, attributes), other) => (query + " " + other.query) -> (attributes ++ other.attributes)
    }

    new Partial {
      def partial = new PartialStatement(q, a)
    }
  }

  def equivalentTo(other: Partial) = {
    val PartialStatement(thisQuery, thisAttributes) = this.partial
    val PartialStatement(otherQuery, otherAttributes) = other.partial
    thisQuery == otherQuery && thisAttributes == otherAttributes
  }
}
