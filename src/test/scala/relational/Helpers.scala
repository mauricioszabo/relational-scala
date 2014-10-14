package test.relational

import org.scalatest.matchers._
import relational.attributes._
import relational.tables._
import relational.Adapter

trait Helpers extends ShouldMatchers {
  val name = new Attribute(new Table("examples"), "name")
  val age = new Attribute(new Table("examples"), "age")

  def pseudoSQL(a: AttributeLike)(implicit ad: Adapter) = a.partial.toPseudoSQL
}
