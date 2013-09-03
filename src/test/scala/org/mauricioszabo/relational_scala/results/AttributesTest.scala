package tests.results

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import org.mauricioszabo.relational_scala.results._

class AttributesTest extends WordSpec with ShouldMatchers {
  lazy val row = new Attributes(Seq('id -> Attribute(1), 'name -> Attribute("Foo")))

  "Attributes accessing" should {
    "access with 'attribute' method" in {
      val n = row attribute 'id as Int
      n should be === 1
    }

    "access by dynamic dispatch" in {
      val n = row.id[Int]
      val n2: Int = row.id
      n should be === 1
      n should be === n2
    }

    "understand null" in {
      val row = new Attributes(Seq('id -> Attribute(1), 'name -> Attribute(null)))
      row.name.isNull should be === true
    }
  }
}
