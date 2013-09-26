package tests.orm

import org.scalatest._

import org.mauricioszabo.relational_scala.orm._

class Person extends Mapping {
  val name = attr.name.as[Property, String]
  val mappings: Map[Symbol, Any] = Map('a -> 10)
}

class MapperTest extends WordSpec with matchers.ShouldMatchers with tests.DatabaseSetup {
  object People extends Mapper[Person] {
    pk = 'id
    table = "scala_people"
    getConnection = { () => connection }
  }

  "Mapper" should {
    "find all records" in {
      val names = People.map { p => p.name.value }
      names.toList should be === List("Foo", "Foo", "Bar")
    }

    //"find a record by primary key" in {
    //  val person = People find 3
    //  person.name.value should be === "Bar"
    //}
  }
}
