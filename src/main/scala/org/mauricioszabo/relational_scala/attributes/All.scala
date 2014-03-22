package relational.attributes

import relational.PartialStatement

class All extends AttributeLike {
  lazy val partial = new PartialStatement("*", Nil)
}
