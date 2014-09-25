package relational.orm.mapper

import relational.results.Attributes

trait IdentityMap[A] {
  def mapTo(attributes: Attributes): A
}
