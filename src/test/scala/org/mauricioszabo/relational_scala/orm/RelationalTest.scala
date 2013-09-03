package tests.orm

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

import org.mauricioszabo.relational_scala.orm._

class RelationalTest extends WordSpec with tests.DatabaseSetup with ShouldMatchers {
  class Person(p: (Symbol, Any) *) extends Relational(p) {
    val name: Property[String] = attr('name)
    val age: Property[Int] = attr('age)
  }

  "Relational Attributes" should {
    "be present" in {
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
  }
}
