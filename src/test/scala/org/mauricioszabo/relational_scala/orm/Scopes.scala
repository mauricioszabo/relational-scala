package tests.orm

import org.scalatest._

import relational.comparissions.Comparission
import relational.orm._
import relational.orm.mapper.LazyResultSet
import relational.comparissions.Comparission

class OtherPerson extends Mapping {
  val name = attr.name.as[Property, String]
  val age = attr.age.as[Property, Int]
  val mappings: Map[Symbol, Any] = Map('a -> 10)
}

class Scopes extends WordSpec with matchers.ShouldMatchers with tests.DatabaseSetup {
  object People extends Mapper[OtherPerson] {
    table = "scala_people"

    defScope ('seventeen) { s =>
      s.where { p => p('age) == 17 }
    }

    defScope ('orFoo) { s =>
      s.where { p => s.where && p('name) == "Foo" }
    }

    getConnection = { () => globalConnection }
  }

  "Scoped search" should {
    "find with a scope" in {
      val p = People.find('seventeen).map(_.age.value)
      p.toList should be === List(17, 17)
    }

    "find with two scopes" in {
      val p = People.find('seventeen).find('orFoo).map(_.age.value)
      p.toList should be === List(17)
    }
  }
}
