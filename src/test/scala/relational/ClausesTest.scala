package tests

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import relational.tables
import relational.clauses._

class ClausesTest extends WordSpec with ShouldMatchers with DatabaseSetup {
  val table = new tables.Table("foo")

  "Select" should {
    "create a SELECT clause" in {
      val select = new Select(false, table, 'one, 'two)
      select.partial.toPseudoSQL should be === "SELECT \"foo\".\"one\", \"foo\".\"two\""
    }

    "create a SELECT DISTINCT clause" in {
      val select = new Select(true, table, 'one, 'two)
      select.partial.toPseudoSQL should be === "SELECT DISTINCT \"foo\".\"one\", \"foo\".\"two\""
    }
  }

  "Partial" should {
    "join with another" in {
      val comp = table('id) == 10
      val select = new Select(false, table, 'one)
      select.appendPartial(comp).partial.toPseudoSQL should be === "SELECT \"foo\".\"one\" \"foo\".\"id\" = 10"
    }
  }
}
