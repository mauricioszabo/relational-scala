package relational.functions

import relational.attributes.AttributeLike

trait Aggregation {
  def aggregated: AttributeLike
}
