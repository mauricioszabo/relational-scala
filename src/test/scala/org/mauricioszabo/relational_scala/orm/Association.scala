package tests.orm

import org.scalatest._

import org.mauricioszabo.relational_scala.orm._
import org.mauricioszabo.relational_scala.orm.mapper.LazyResultSet

class AssociationAddress extends Mapping {
  val mappings: Map[Symbol, Any] = Map()
}

class AssociationPerson extends Mapping {
  val name = attr.name.as[Property, String]
  val mappings: Map[Symbol, Any] = Map()

  val addresses = is[Many, AssociationAddress].query { selector =>
    AssociationAddress where { a => a.id in selector.select('id) }
  }
}

class Association extends WordSpec with matchers.ShouldMatchers with tests.DatabaseSetup {

  object People extends Mapper[Person] {
    pk = 'id
    table = "scala_people"
    getConnection = { () => globalConnection }
  }

  "Many associations" should {
    "retrieve the records by SQL" in {
      val p = People.find(1)
    }
  }
}
