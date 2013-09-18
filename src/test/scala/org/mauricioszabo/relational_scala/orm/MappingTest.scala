package tests.orm

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import org.mauricioszabo.relational_scala.orm._

class MappingTest extends WordSpec with tests.DatabaseSetup with ShouldMatchers {
  class Person(p: (Symbol, Any) *) extends Mapping {
    val mappings = Map(p: _*)

    val name = attr('name).as[Property, String]
    val email = attr.email.as[Formatted, String].withFormat("(.*)@(.*)".r)
  }

  "Mapping" should {
    val p = new Person('email -> "Foo")
    "fails validation if any attribute fails" in {
      p.isValid should not be true
    }

    "give a list of errors if is not valid" in {
      val Invalid(_, errors) = p.valid
      errors('email) should be === List( ("orm.mapping.errors.formatted", Nil) )
    }

    "translates the errors" in {
      val Invalid(_, errors) = p.valid
      errors.list should be === List( "Email is in invalid format" )
    }
  }
}
