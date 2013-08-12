import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

import org.mauricioszabo.relational_scala._

class AttributeTest extends WordSpec with ShouldMatchers {
  lazy val table = new tables.Table("examples")
  lazy val name = new attributes.Attribute(table, "name")
  lazy val id = table('id)

  "Attribute" should {
    "have a representation" in {
      attributeSql(name) should be === "examples.name"
    }

    "represent every attribute on a table" in {
      attributeSql(table.*) should be === "examples.*"
    }
  }

  "Attributes comparission" should {
    "define equality with values" in {
      attributeSql(name == "Foo's Bar") should be === "examples.name = 'Foo''s Bar'"
    }

    "compare 'AnyVal' attributes like INT or DOUBLE" in {
      attributeSql(id == 20) should be === "examples.id = 20"
    }

    "define inequality with values" in {
      attributeSql(name <= "Foo") should be === "examples.name <= 'Foo'"
      attributeSql(name < "Foo") should be === "examples.name < 'Foo'"
      attributeSql(name >= "Foo") should be === "examples.name >= 'Foo'"
      attributeSql(name > "Foo") should be === "examples.name > 'Foo'"
      attributeSql(name != "Foo") should be === "examples.name <> 'Foo'"
    }

    "define equality with NULL" in {
      attributeSql(name.isNull) should be === "examples.name IS NULL"
      attributeSql(name == null) should be === "examples.name IS NULL"
      attributeSql(name != null) should be === "examples.name IS NOT NULL"
      attributeSql(name < null) should be === "examples.name < NULL"
    }

    "define equality with LIKE and NOT LIKE" in {
      attributeSql(name =~ "Foo") should be === "examples.name LIKE 'Foo'"
      attributeSql(name like "Foo") should be === "examples.name LIKE 'Foo'"
      attributeSql(name !~ "Foo") should be === "examples.name NOT LIKE 'Foo'"
      attributeSql(name notLike "Foo") should be === "examples.name NOT LIKE 'Foo'"
    }

    "define equality and inequality with other attributes" in {
      attributeSql(name == id) should be === "examples.name = examples.id"
      attributeSql(name != id) should be === "examples.name <> examples.id"
    }

    "negate a whole condition" in {
      val result = !(name <= "Foo")
      attributeSql(result) should be === "NOT(examples.name <= 'Foo')"
    }

    "find IN a list of parameters" in {
      val result = (name in List("Foo", "Bar"))
      attributeSql(result) should be === "examples.name IN ('Foo','Bar')"
    }

    "add an OR or AND condition" in {
      val c1 = (name == "Foo")
      val c2 = (id == 10)
      attributeSql(c1 || c2) should be === "(examples.name = 'Foo' OR examples.id = 10)"
      attributeSql(c1 && c2) should be === "(examples.name = 'Foo' AND examples.id = 10)"
    }

    "support SUM, AVERAGE, MAX, MIN, COUNT" in {
      attributeSql(id.sum == 20) should be === "SUM(examples.id) = 20"
      attributeSql(id.avg == 15) should be === "AVG(examples.id) = 15"
      attributeSql(id.max == 15) should be === "MAX(examples.id) = 15"
      attributeSql(id.min == 15) should be === "MIN(examples.id) = 15"
      attributeSql(id.count == 15) should be === "COUNT(examples.id) = 15"
    }

    "support LENGTH, UPPER, LOWER" in {
      attributeSql(name.length == 1) should be === "LENGTH(examples.name) = 1"
      attributeSql(name.upper == "UP") should be === "UPPER(examples.name) = 'UP'"
      attributeSql(name.lower == "up") should be === "LOWER(examples.name) = 'up'"
    }
  }

  "Attributes ordering" should {
    "define order in SQL clauses" in {
      orders.Ascending(name).partial.toPseudoSQL should be === "(examples.name) ASC"
      orders.Descending(name).partial.toPseudoSQL should be === "(examples.name) DESC"
    }
  }

  def attributeSql(a: attributes.AttributeLike) = a.partial.toPseudoSQL
}
