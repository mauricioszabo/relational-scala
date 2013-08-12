import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import org.mauricioszabo.relational_scala._

class QueryTest extends WordSpec with ShouldMatchers with DatabaseSetup {
  object People extends Query {
    table = "scala_people"
  }

  "A selector" should {
    "copy with a Query" in {
      val table = new tables.Table("ex")
      val selector = Selector(select=List(table('id)), from=List(table))
      val newSelector = selector.copy(select=List(table('name)))
      newSelector.isInstanceOf[Query] should be === true
      newSelector.partial.toPseudoSQL should be === "SELECT ex.name FROM ex"
    }
  }

  "A Query" should {
    "find records on my database" in {
      val two = People where (p => p('id) > 1 && p('id) < 3) select ('id, 'name)
      results(two) should be === List((2, "Foo"))
    }

    "finds records reusing 'table' object" in {
      val two = People query { p => p where (p('id) > 1 && p('id) < 3) select (p('id), p('name)) }
      results(two) should be === List((2, "Foo"))
    }
  }

  "Query using implicit conversions" should {
    "find records using symbols" in {
      var two = People where (implicit p => 'id > 1 && 'id < 3) select ('id, 'name)
      results(two) should be === List((2, "Foo"))

      two = People where (implicit p => 'id -> 2) select ('id, 'name)
      results(two) should be === List((2, "Foo"))
    }

    "find records using symbols and 'query' method" in {
      val two = People query { implicit p => p where ('id > 1 && 'id < 3) select ('id, 'name) }
      results(two) should be === List((2, "Foo"))
    }
  }

  def results(sel: Selector) = sel.copy(connection=connection).results.map { e =>
    (e attribute 'id as Int, e get 'name)
  }.toList
}
