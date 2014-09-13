package test.relational

import org.scalatest._

import relational._
import relational.{attributes => attrs}

class AttributeTest extends WordSpec with matchers.ShouldMatchers {
  implicit val adapter = new Adapter
  adapter configure 'all
  lazy val table = new tables.Table("examples")
  lazy val name = new attrs.Attribute(table, "name")
  lazy val id = table('id)

  //TODO: add a case for SELECT DISTINCT, and COUNT(DISTINCT attr)

  "Attribute" should {
    "have a representation" in {
      pseudoSQL(name) should be === "\"examples\".\"name\""
    }

    "represent every attribute on a table" in {
      pseudoSQL(table.*) should be === "\"examples\".*"
    }

    "represent a single attribute dynamically" in {
      pseudoSQL(table.name) should be === "\"examples\".\"name\""
    }

    "represent a literal" in {
      pseudoSQL(attrs.Attribute.wrap(10)) should be === "10"
      pseudoSQL(attrs.Attribute.wrap("foo")) should be === "'foo'"
      pseudoSQL(attrs.Attribute.wrap(name)) should be === "\"examples\".\"name\""
    }
  }

  "Attributes comparission" should {
    "define equality with values" in {
      pseudoSQL(name == "Foo's Bar") should be === "\"examples\".\"name\" = 'Foo''s Bar'"
    }

    "compare 'AnyVal' attributes like INT or DOUBLE" in {
      pseudoSQL(id == 20) should be === "\"examples\".\"id\" = 20"
    }

    "define inequality with values" in {
      pseudoSQL(name <= "Foo") should be === "\"examples\".\"name\" <= 'Foo'"
      pseudoSQL(name < "Foo") should be === "\"examples\".\"name\" < 'Foo'"
      pseudoSQL(name >= "Foo") should be === "\"examples\".\"name\" >= 'Foo'"
      pseudoSQL(name > "Foo") should be === "\"examples\".\"name\" > 'Foo'"
      pseudoSQL(name != "Foo") should be === "\"examples\".\"name\" <> 'Foo'"
    }

    "define equality with NULL" in {
      pseudoSQL(name.isNull) should be === "\"examples\".\"name\" IS NULL"
    }

    "define equality with LIKE and NOT LIKE" in {
      pseudoSQL(name =~ "Foo") should be === "\"examples\".\"name\" LIKE 'Foo'"
      pseudoSQL(name like "Foo") should be === "\"examples\".\"name\" LIKE 'Foo'"
      pseudoSQL(name !~ "Foo") should be === "\"examples\".\"name\" NOT LIKE 'Foo'"
      pseudoSQL(name notLike "Foo") should be === "\"examples\".\"name\" NOT LIKE 'Foo'"
    }

    "define equality and inequality with other attributes" in {
      pseudoSQL(name == id) should be === "\"examples\".\"name\" = \"examples\".\"id\""
      pseudoSQL(name != id) should be === "\"examples\".\"name\" <> \"examples\".\"id\""
    }

    "negate a whole condition" in {
      val result = !(name <= "Foo")
      pseudoSQL(result) should be === "NOT(\"examples\".\"name\" <= 'Foo')"
    }

    "find IN a list of parameters" in {
      val result = (name in List("Foo", "Bar"))
      pseudoSQL(result) should be === "\"examples\".\"name\" IN ('Foo','Bar')"
    }

    "add an OR or AND condition" in {
      val c1 = (name == "Foo")
      val c2 = (id == 10)
      pseudoSQL(c1 || c2) should be === "(\"examples\".\"name\" = 'Foo' OR \"examples\".\"id\" = 10)"
      pseudoSQL(c1 && c2) should be === "(\"examples\".\"name\" = 'Foo' AND \"examples\".\"id\" = 10)"
    }

    "support SUM, AVERAGE, MAX, MIN, COUNT" in {
      pseudoSQL(id.sum == 20) should be === "SUM(\"examples\".\"id\") = 20"
      pseudoSQL(id.avg == 15) should be === "AVG(\"examples\".\"id\") = 15"
      pseudoSQL(id.max == 15) should be === "MAX(\"examples\".\"id\") = 15"
      pseudoSQL(id.min == 15) should be === "MIN(\"examples\".\"id\") = 15"
      pseudoSQL(id.count == 15) should be === "COUNT(\"examples\".\"id\") = 15"
    }

    "support LENGTH, UPPER, LOWER" in {
      pseudoSQL(name.length == 1) should be === "LENGTH(\"examples\".\"name\") = 1"
      pseudoSQL(name.upper == "UP") should be === "UPPER(\"examples\".\"name\") = 'UP'"
      pseudoSQL(name.lower == "up") should be === "LOWER(\"examples\".\"name\") = 'up'"
    }
  }

  "Attributes ordering" should {
    "define order in SQL clauses" in {
      orders.Ascending(name).partial.toPseudoSQL should be === "(\"examples\".\"name\") ASC"
      orders.Descending(name).partial.toPseudoSQL should be === "(\"examples\".\"name\") DESC"
    }
  }

  "NULL attribute" should {
    import comparissions.None
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
  }

  def pseudoSQL(a: attrs.AttributeLike) = a.partial.toPseudoSQL
}
