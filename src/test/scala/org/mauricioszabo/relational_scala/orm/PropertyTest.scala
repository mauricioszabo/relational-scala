package tests.orm

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import org.mauricioszabo.relational_scala.orm._

class PropertyTest extends WordSpec with tests.DatabaseSetup with ShouldMatchers {
  class Person(p: (Symbol, Any) *) extends Mapping {
    val mappings = p
    val name = attr('name).as[Property, String]
    val age = attr('age).as[Property, Int]
    val email = attr('email).as[Formatted, String].withFormat("(.*)@(.*)".r)
    val present = attr('present).as[Required, String].validateIf { name.value != "ADMIN" }
    val function = attr('function).as[Validated, String].validIf { f => f.value != "INVALID" }
  }

  "Property validations" should {
    "be present and valid" in {
      val p = new Person('name -> "Foo Bar", 'age -> 21)
      p.name.value should be === "Foo Bar"
      p.age.value should be === 21
    }

    "define if attribute is present" in {
      val p = new Person('name -> "Foo Bar")
      p.age.isPresent should be (false)
      p.age.value should be === 0
    }

    "be not present if is null" in {
      val p = new Person('age -> null)
      p.age.isPresent should be (false)
      p.age.value should be === 0
    }

    "be not present if typecast fails" in {
      val p = new Person('age -> "Twelve")
      p.age.isPresent should be (false)
      p.age.value should be === 0
    }

    "validate format of some string" in {
      //FIXME: valid must be in the Mapping
      val p = new Person('email -> "foo")
      p.email should not be('valid)
      val p2 = new Person('email -> "foo@bar.com")
      p2.email should be('valid)
    }

    "validate presence" in {
      val p = new Person('present -> "")
      p.present should not be('valid)
    }

    "permits to skip validation if some condition is met" in {
      val p = new Person('present -> "", 'name -> "ADMIN")
      p.present should be('valid)
    }

    "validates with a function" in {
      val p = new Person('function -> "INVALID")
      p.function should not be('valid)
    }
  }
}

