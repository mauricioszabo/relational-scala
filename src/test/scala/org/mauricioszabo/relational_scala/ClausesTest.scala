package tests

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import org.mauricioszabo.relational_scala.tables
import org.mauricioszabo.relational_scala.clauses._

class ClausesTest extends WordSpec with ShouldMatchers with DatabaseSetup {
  val table = new tables.Table("foo")

  "Select" should {
    "create a SELECT clause" in {
      val select = new Select(false, table, 'one, 'two)
      select.partial.toPseudoSQL should be === "SELECT foo.one, foo.two"
    }

    "create a SELECT DISTINCT clause" in {
      val select = new Select(true, table, 'one, 'two)
      select.partial.toPseudoSQL should be === "SELECT DISTINCT foo.one, foo.two"
    }
  }
}
