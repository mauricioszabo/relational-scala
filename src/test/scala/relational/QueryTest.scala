package tests

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import relational._
import relational.functions._

class QueryTest extends WordSpec with ShouldMatchers with DatabaseSetup {
  object People extends Query {
    table = "scala_people"
  }

  "A Query" should {
    "find all records on a table" in {
      val result = People.all
      results(result).size should be === 3
    }

    "find records on a table" in {
      val two = People where (p => p('id) > 1 && p('id) < 3) select ('id, 'name)
      results(two) should be === List((2, "Foo"))
    }

    "find records using HAVING" in {
      val two = People having (p => p('id) > 1 && p('id) < 3) select ('id, 'name) group ('id)
      results(two) should be === List((2, "Foo"))
    }

    "find records reusing 'table' object" in {
      val two = People query { p => p where (p('id) > 1 && p('id) < 3) select (p('id), p('name)) }
      results(two) should be === List((2, "Foo"))
    }

    "finds distinct records" in {
      val people = new tables.Table("scala_people").as("sp")
      val two = People distinct ('id, 'name) from ('scala_people, people)
      results(two).size should be === 3
    }

    "join another table" in {
      val address = People join 'scala_addresses on { (p, a) => p('id) == a('person_id) }
      results(address) should be === List((1, "Foo"), (1, "Foo"), (2, "Foo"))
    }

    "join another with a join object" in {
      val table = new tables.Table("scala_addresses")
      val join = new joins.InnerJoin(table, table('person_id) == People.table('id))
      val address = People join Seq(join)
      results(address) should be === List((1, "Foo"), (1, "Foo"), (2, "Foo"))
    }

    "left join another table" in {
      val address = People leftJoin 'scala_addresses on { (p, a) => p('id) == a('person_id) }
      results(address) should be === List((1, "Foo"), (1, "Foo"), (2, "Foo"), (3, "Bar"))
    }

    "count records on table" in {
      pending
      val names = People query { implicit p => p select (Count('name).as("count"), 'name) group 'name }

      val results = names.copy(connection=globalConnection).results.map { e =>
        (e attribute 'count as Int, e get 'name)
      }
      results should be === List( (2, "Foo"), (1, "Bar") )
    }

    "order the query" in {
      val desc = People query { implicit p => p order 'id.desc }
      results(desc) should be === List( (3, "Bar"), (2, "Foo"), (1, "Foo") )
    }

    "subselect another query" in {
      val desc = People query { implicit p => p order 'id.desc }
      val r = People query { implicit p => p from desc.as("bar") where { p => p('name) -> "Foo" } }
      results(r) should be === List( (2, "Foo"), (1, "Foo") )
    }

    "order the query with a subselect" in {
      object Addresses extends Query { table = "scala_addresses" }

      val primaryQuery = People select '*
      val r = primaryQuery order {
        Addresses where { a => a('id) == primaryQuery.table('id) } select 'address
      }

      r.partial.toPseudoSQL should be === ("SELECT \"scala_people\".* FROM \"scala_people\" ORDER BY " +
        "(SELECT \"scala_addresses\".\"address\" FROM \"scala_addresses\" WHERE \"scala_addresses\".\"id\" = \"scala_people\".\"id\")")
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

  "Query on more than one table" should {
    "cast to the first table in FROM" in {
      val desc = People query { implicit p => p order 'id.desc }
      val result = People query { implicit p => p
        .from(desc as "sql")
        .order { p => Seq(p.name) }
        .join('scala_addresses).on { (p, a) => p.id == a.person_id }
        .select('id)
      }

      result.partial.toPseudoSQL should be === (
        "SELECT \"sql\".\"id\" FROM (SELECT * FROM \"scala_people\" ORDER BY (\"scala_people\".\"id\") DESC) \"sql\" " +
        "INNER JOIN \"scala_addresses\" ON \"sql\".\"id\" = \"scala_addresses\".\"person_id\" " +
        "ORDER BY \"sql\".\"name\""
      )
    }
  }

  def results(sel: Selector) = {
    val query = new Selector(sel.copy(connection=globalConnection)) with Query
    query.results.map { e => (e attribute 'id as Int, e get 'name) }.toList
  }
}
