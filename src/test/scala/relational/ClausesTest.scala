package tests

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import relational.tables
import relational.clauses._

class ClausesTest extends WordSpec with ShouldMatchers with test.relational.Helpers {
  val table = new tables.Table("foo")

  "Select" should {
    "create a SELECT * clause" in {
      val select = new Select(false, table, '*)
      select.partial.toPseudoSQL should be === "SELECT \"foo\".*"
    }

    "create a SELECT clause with alias" in {
      val select = new Select(false, table, 'one, name.as("n"))
      select.partial.toPseudoSQL should be === """SELECT "foo"."one", "examples"."name" "n""""
    }

    "create a SELECT DISTINCT clause" in {
      val select = new Select(true, table, 'one, 'two)
      select.partial.toPseudoSQL should be === "SELECT DISTINCT \"foo\".\"one\", \"foo\".\"two\""
    }
  }
}
