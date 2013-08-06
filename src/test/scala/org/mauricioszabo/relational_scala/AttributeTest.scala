import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import org.mauricioszabo.relational_scala._

class AttributeTest extends FlatSpec with ShouldMatchers {
  lazy val table = new tables.Table("examples")
  lazy val name = new attributes.Table(table, "name")
  lazy val id = table('id)

  "Attribute" should "have a representation" in {
    name.representation should be === "examples.name"
  }

  it should "represent every attribute on a table" in {
    table.*.representation should be === "examples.*"
  }


  "Attributes comparission" should "define equality with values" in {
    val result = (name == "Foo's Bar")
    result.partial.toPseudoSQL should be === "examples.name = 'Foo''s Bar'"
  }

  it should "compare 'AnyVal' attributes like INT or DOUBLE" in {
    val result = (id == 20)
    result.partial.toPseudoSQL should be === "examples.id = 20"
  }

  it should "define inequality with values" in {
    (name <= "Foo").partial.toPseudoSQL should be === "examples.name <= 'Foo'"
    (name < "Foo").partial.toPseudoSQL should be === "examples.name < 'Foo'"
    (name >= "Foo").partial.toPseudoSQL should be === "examples.name >= 'Foo'"
    (name > "Foo").partial.toPseudoSQL should be === "examples.name > 'Foo'"
    (name != "Foo").partial.toPseudoSQL should be === "examples.name <> 'Foo'"
  }

  it should "define equality with NULL" in {
    (name.isNull).partial.toPseudoSQL should be === "examples.name IS NULL"
    (name == null).partial.toPseudoSQL should be === "examples.name IS NULL"
    (name != null).partial.toPseudoSQL should be === "examples.name IS NOT NULL"
    (name < null).partial.toPseudoSQL should be === "examples.name < NULL"
  }

  it should "define equality with LIKE and NOT LIKE" in {
    (name =~ "Foo").partial.toPseudoSQL should be === "examples.name LIKE 'Foo'"
    (name like "Foo").partial.toPseudoSQL should be === "examples.name LIKE 'Foo'"
    (name !~ "Foo").partial.toPseudoSQL should be === "examples.name NOT LIKE 'Foo'"
    (name notLike "Foo").partial.toPseudoSQL should be === "examples.name NOT LIKE 'Foo'"
  }

  it should "define equality and inequality with other attributes" in {
    (name == id).partial.toPseudoSQL should be === "examples.name = examples.id"
    (name != id).partial.toPseudoSQL should be === "examples.name <> examples.id"
  }

  it should "negate a whole condition" in {
    val result = !(name <= "Foo")
    result.partial.toPseudoSQL should be === "NOT(examples.name <= 'Foo')"
  }

  it should "find IN a list of parameters" in {
    val result = (name in List("Foo", "Bar"))
    result.partial.toPseudoSQL should be === "examples.name IN ('Foo','Bar')"
  }

  it should "add an OR or AND condition" in {
    val c1 = (name == "Foo")
    val c2 = (id == 10)
    (c1 || c2).partial.toPseudoSQL should be === "(examples.name = 'Foo' OR examples.id = 10)"
    (c1 && c2).partial.toPseudoSQL should be === "(examples.name = 'Foo' AND examples.id = 10)"
  }

  it should "support SUM, AVERAGE, MAX, MIN, COUNT" in {
    (id.sum == 20).partial.toPseudoSQL should be === "SUM(examples.id) = 20"
    (id.avg == 15).partial.toPseudoSQL should be === "AVG(examples.id) = 15"
    (id.max == 15).partial.toPseudoSQL should be === "MAX(examples.id) = 15"
    (id.min == 15).partial.toPseudoSQL should be === "MIN(examples.id) = 15"
    (id.count == 15).partial.toPseudoSQL should be === "COUNT(examples.id) = 15"
  }

  it should "support LENGTH, UPPER, LOWER" in {
    (name.length == 1).partial.toPseudoSQL should be === "LENGTH(examples.name) = 1"
    (name.upper == "UP").partial.toPseudoSQL should be === "UPPER(examples.name) = 'UP'"
    (name.lower == "up").partial.toPseudoSQL should be === "LOWER(examples.name) = 'up'"
  }
}

