package tests.orm

import org.scalatest._

import relational.orm._
import relational.orm.mapper.LazyResultSet

class Person extends Mapping {
  val name = attr.name.as[Property, String]
  val mappings: Map[Symbol, Any] = Map('a -> 10)
}

class MapperTest extends WordSpec with matchers.ShouldMatchers with tests.DatabaseSetup {
  object People extends Mapper[Person] {
    pk = 'id
    table = "scala_people"
    getConnection = { () => globalConnection }
  }

  "Mapper" should {
    "find all records" in {
      val names = People.map { p => p.name.value }
      names.toList should be === List("Foo", "Foo", "Bar")
    }

    "find a record by primary key" in {
      val person = People find 3
      person.name.value should be === "Bar"
    }
  }

  "Mapper restriction" should {
    "concatenate conditions using AND" in {
      val q1 = People where { p => p('name) == "Foo" }
      val q2 = People where { p => p('name) == "Bar" }
      val result = q2 restrict q1

      result.partial.toPseudoSQL should be === (
        "SELECT * FROM scala_people WHERE (scala_people.name = 'Foo' AND scala_people.name = 'Bar')"
      )
    }

    "add joins" in {
      val sql = restrict(
        People join 'foo on { (p, f) => p('id) == f('people_id) },
        People join 'bar on { (p, f) => p('id) == f('people_id) }
      )

      sql should include ("INNER JOIN foo ON scala_people.id = foo.people_id")
      sql should include ("INNER JOIN bar ON scala_people.id = bar.people_id")
    }

    "not add joins if there is one equivalent" in {
      val sql = restrict(
        People join 'foo on { (p, f) => p('id) == f('people_id) },
        People join 'foo on { (p, f) => p('id) == f('people_id) }
      )
      sql should be === ("SELECT * FROM scala_people INNER JOIN foo ON scala_people.id = foo.people_id")
    }

    "concatenate HAVING conditions" in {
      val sql = restrict(
        People having { p => p('name) == "Foo" },
        People having { p => p('name) == "Bar" }
      )

      sql should include ("HAVING (scala_people.name = 'Foo' AND scala_people.name = 'Bar')")
    }

    def restrict(q1: LazyResultSet[Person], q2: LazyResultSet[Person]) = q2.restrict(q1).partial.toPseudoSQL
  }
}
