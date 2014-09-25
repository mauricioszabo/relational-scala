package test.relational

import org.scalatest.matchers._
import relational.attributes._
import relational.tables._

trait Helpers extends ShouldMatchers {
  val name = new Attribute(new Table("examples"), "name")

  def pseudoSQL(a: AttributeLike) = a.partial.toPseudoSQL
}
