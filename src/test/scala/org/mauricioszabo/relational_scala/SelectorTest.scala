package tests

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import org.mauricioszabo.relational_scala._
import org.mauricioszabo.relational_scala.clauses.Select

class SelectorTest extends WordSpec with DatabaseSetup with ShouldMatchers {
  val people = new tables.Table("scala_people")

  //import scala.reflect.runtime.universe._
  //def foo[A](a: A)(implicit tag: TypeTag[A]) = tag.tpe
  //typeOf[Something].members.filter(m => m.isTerm && (m.asTerm.isVal || m.asTerm.isVar))

  "Selector" should {
    val select = Select.select(people, people.*)
    val selector = Selector(from=List(people), select=select)

    "create a SQL" in {
      selector.partial.toPseudoSQL should be === "SELECT scala_people.* FROM scala_people"

      val selector2 = selector.copy(where=(people("id") == 10))
      selector2.partial.toPseudoSQL should be === "SELECT scala_people.* FROM scala_people WHERE scala_people.id = 10"
    }

    "search for records in a database" in {
      val selector2 = selector.copy(where=(people("id") <= 2), connection=connection)
      val ids = selector2.results.map(_ attribute 'id as Int)
      ids.toList should be === List(1, 2)
    }

    "search with a string" in {
      val selector2 = selector.copy(where=(people("name") == "Foo"), connection=connection)
      val ids = selector2.results.map(_ attribute 'id as Int)
      ids.toList should be === List(1, 2)
    }

    "select using operations" in {
      val selector2 = selector.copy(
        select=Select.select(people, people('id) == 1),
        connection=connection
      )
      val results = selector2.results.map(_.attribute.values.toList(0).value).toList
      results should be === List("true", "false", "false")
    }

    "With pagination" should {
      "paginate the results" in {
        val s = selector.copy(limit=1, offset=2)
        s.partial.toPseudoSQL should be === "SELECT scala_people.* FROM scala_people LIMIT 1 OFFSET 2"
      }
    }

    "search with a date" in {
      pending
      val selector = Selector(from=List(people), select=select,
        where=(people("birth") < java.sql.Date.valueOf("1990-01-01")), connection=connection)
      val ids = selector.results.map(_ attribute 'id as Int)
      ids.toList should be === List(1, 3)
    }
  }
}
