package test

import org.scalatest._
import org.mauricioszabo.relational_scala._
//import org.mauricioszabo.relational_scala.joins._

class Equals extends WordSpec with matchers.ShouldMatchers {
  "Partials" should {
    "calculate equivalences with Tables" in {
      val t1 = new tables.Table("examples")
      val t2 = new tables.Table("examples")

      (t1 equivalentTo t2) should be (true)
    }

    "calculate equivalences with attributes" in {
      val n1 = new tables.Table("examples")('name)
      val n2 = new tables.Table("examples")('name)

      (n1 equivalentTo n2) should be (true)
    }

    "calculate equivalences with simple comparissions" in {
      val n1 = new tables.Table("examples")('name) == "Foo"
      val n2 = new tables.Table("examples")('name) == "Foo"
      val n3 = new tables.Table("examples")('name) == "Bar"

      (n1 equivalentTo n2) should be (true)
      (n1 equivalentTo n3) should be (false)
    }
  }

  "Comparission's equivalences" should {
    val c1 = new tables.Table("examples")('name) == "Foo"
    val c2 = new tables.Table("examples")('name) == "Bar"

    "ignore order of OR" in {
      pending
      val o1 = c1 || c2
      val o2 = c2 || c1
      (o1 equivalentTo o2) should be (true)
    }
  }
}
