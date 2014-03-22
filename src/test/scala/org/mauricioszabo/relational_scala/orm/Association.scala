package tests.orm

import org.scalatest._

import relational.orm._
import relational.orm.mapper.LazyResultSet

class AssociationAddress extends Mapping {
  val mappings: Map[Symbol, Any] = Map()
}

class AssociationPerson extends Mapping {
  val name = attr.name.as[Property, String]
  val mappings: Map[Symbol, Any] = Map()

  //val addresses = is[Many[AssociationAddress], AssociationAddress].query { selector =>
  //  AssociationAddress where { a => a.id in selector.select('id) }
  //}
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
