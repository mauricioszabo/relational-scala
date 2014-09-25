package test.relational.comparissions

import relational.comparissions._
import relational.attributes.{Attribute, AttributeLike}
import relational.tables.Table
import org.scalatest._

class NoneTest extends WordSpec with matchers.ShouldMatchers {
  val name = new Attribute(new Table("examples"), "name")

  "NULL attribute" should {
    val nameFoo = name == "foo"

    "not enter in OR" in {
      pseudoSQL(None || nameFoo) should be === "\"examples\".\"name\" = 'foo'"
      pseudoSQL(nameFoo || None) should be === "\"examples\".\"name\" = 'foo'"
    }

    "not enter in AND" in {
      pseudoSQL(None && nameFoo) should be === "\"examples\".\"name\" = 'foo'"
      pseudoSQL(nameFoo && None) should be === "\"examples\".\"name\" = 'foo'"
    }

    "not enter in NOT" in {
      pseudoSQL(!None) should be === ""
    }

    def pseudoSQL(a: AttributeLike) = a.partial.toPseudoSQL
  }

  "None" should {
    "check for equality" in {
      (name == None) should be (None)
      (name != None) should be (None)
      (name <= None) should be (None)
      (name <  None) should be (None)
      (name >= None) should be (None)
      (name >  None) should be (None)
      (name =~ None) should be (None)
      (name !~ None) should be (None)

      (None == name) should be (None)
      (None != name) should be (None)
      (None <= name) should be (None)
      (None <  name) should be (None)
      (None >= name) should be (None)
      (None >  name) should be (None)
      (None =~ name) should be (None)
      (None !~ name) should be (None)
    }

    "check for IN" in {
      (None in List(1, 2, 3, 4, 5)) should be (None)
    }

    "check for NULL" in {
      (None.isNull) should be (None)
    }
  }
}
