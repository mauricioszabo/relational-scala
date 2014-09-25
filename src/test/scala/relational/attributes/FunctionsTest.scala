package test.relational.comparissions

import relational.Adapter
import relational.functions._
import relational.tables.Table
import org.scalatest._

class FunctionsTest extends WordSpec with test.relational.Helpers {
  implicit val adapter = new Adapter

  "Function" should {
    "create a function in SQL" in {
      object Foobar extends SqlFunction[String] {
        defineByFunction { case Seq(attribute, param) => {
          case('all) => for {
            a <- attribute.partial
            p <- param.partial
          } yield "FOO_BAR(" + a.query + ", " + p.query + ")" -> (a.params ++ p.params)
        }}
      }

      Foobar(name, 10).partial.toPseudoSQL should be === """FOO_BAR("examples"."name", 10)"""
    }

    "create a function in a simpler way" in {
      object Foobar extends SqlFunction[Int] { define('all -> "FOOBAR($0, $1)") }
      object Foobar2 extends SqlFunction[Int] { define('all -> "$0 FOOBAR($2, $1)") }

      Foobar(name, 10).partial.toPseudoSQL should be === "FOOBAR(\"examples\".\"name\", 10)"
      Foobar2(name, 10, 20).partial.toPseudoSQL should be === "\"examples\".\"name\" FOOBAR(20, 10)"
    }

    "create a method that has different behaviour for each adapter" in {
      pending
      object Foobar extends SqlFunction[Int] {
        define('all -> "$0 FOO($1)", 'oracle -> "$0 BAR($1)")
      }

      adapter configure 'mysql
      Foobar(name, 10).partial.toPseudoSQL should be === """"examples"."name" FOO(10)"""

      adapter configure 'oracle
      Foobar(name, 10).partial.toPseudoSQL should be === """"examples"."name" BAR(10)"""
    }
  }
}
