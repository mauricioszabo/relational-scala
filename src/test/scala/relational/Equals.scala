package test.relational

import org.scalatest._
import relational._
//import relational.joins._

class Equals extends WordSpec with matchers.ShouldMatchers {
  val examples = new tables.Table("examples")
  "Partials" should {
    "calculate equivalences with Tables" in {
      val t1 = new tables.Table("examples")
      val t2 = new tables.Table("examples")

      (t1 equivalentTo t2) should be (true)
    }

    "calculate equivalences with attributes" in {
      val n1 = examples('name)
      val n2 = examples('name)

      (n1 equivalentTo n2) should be (true)
    }

    "calculate equivalences with simple comparissions" in {
      val n1 = examples('name) == "Foo"
      val n2 = examples('name) == "Foo"
      val n3 = examples('name) == "Bar"

      (n1 equivalentTo n2) should be (true)
      (n1 equivalentTo n3) should be (false)
    }
  }

  "Comparission's equivalences" should {
    val c1 = examples('name) == "Foo"
    val c2 = examples('name) == "Bar"

    "ignore order of OR" in {
      pending
      val o1 = c1 || c2
      val o2 = c2 || c1
      (o1 equivalentTo o2) should be (true)
    }
  }

  "Comparissions by equalities (default)" should {
    "compare tables" in {
      val t1 = new tables.Table("foo")
      val t2 = new tables.Table("foo")
      (t1 == t2) should be === true
    }
  }
}
