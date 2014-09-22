package test.relational.comparissions

import relational.Adapter
import relational.functions._
import relational.tables.Table
import org.scalatest._

class FunctionsTest extends WordSpec with test.relational.Helpers {
  implicit val adapter = new Adapter

  "Function" should {
    "create a function in SQL" in {
      object Foobar extends SqlFunction {
        //defineByFunction('all -> { case Seq(p, param) =>
        //  (
        //    { a: Adapter => "FOO_BAR(" + p.partial.sql(a) + ", "+ param.partial.sql(a) + ")" },
        //    p.partial.attributes.toVector ++ param.partial.attributes
        //  )
        //})
        defineByFunction(adapter => {
          case 'all => {
            case Seq(p, param) => (
              "FOO_BAR(" + p.partial.sql(a) + ", "+ param.partial.sql(a) + ")",
              p.partial.attributes.toVector ++ param.partial.attributes
            )
          }
        })
      }
      Foobar(name, 10).partial.toPseudoSQL should be === """FOO_BAR("examples"."name", 10)"""
    }

    "create a function in a simpler way" in {
      object Foobar extends SqlFunction { define('all -> "FOOBAR($0, $1)") }
      object Foobar2 extends SqlFunction { define('all -> "$0 FOOBAR($2, $1)") }

      Foobar(name, 10).partial.toPseudoSQL should be === "FOOBAR(\"examples\".\"name\", 10)"
      Foobar2(name, 10, 20).partial.toPseudoSQL should be === "\"examples\".\"name\" FOOBAR(20, 10)"
    }

    "create a method that has different behaviour for each adapter" in {
      object Foobar extends SqlFunction {
        define('all -> "$0 FOO($1)", 'oracle -> "$0 BAR($1)")
      }

      adapter configure 'mysql
      Foobar(name, 10).partial.toPseudoSQL should be === """"examples"."name" FOO(10)"""

      adapter configure 'oracle
      Foobar(name, 10).partial.toPseudoSQL should be === """"examples"."name" BAR(10)"""
    }
  }
}
