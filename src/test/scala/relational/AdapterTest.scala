package test.relational

import org.scalatest._
import relational._

class AdapterTest extends WordSpec with matchers.ShouldMatchers {
  Adapter.configure('all)

  lazy val table = new tables.Table("examples")
  //lazy name = new attributes.Attribute(table, "name")

  class Test extends attributes.Attribute(table, "name") {
    def foobar(param: Any) =
      new attributes.Function('foobar, this, attributes.Attribute.wrap(param))

    def foobar2(p1: Any, p2: Any) =
      new attributes.Function('foobar2, this, attributes.Attribute.wrap(p1), attributes.Attribute.wrap(p2))
  }
  lazy val name = new Test

  "Adapter" should {
    "create a function in SQL" in {
      Adapter.defineFunction('foobar, 'all -> { (p, params) =>
        val param = params.head

        (
          "FOO_BAR(" + p.partial.query + ", "+ param.partial.query + ")",
          p.partial.attributes.toList ++ param.partial.attributes
        )
      })

      name.foobar(10).partial.toPseudoSQL should be === "FOO_BAR(examples.name, 10)"
    }

    "create a function in a simpler way" in {
      Adapter.defineFunctionN('foobar, 'all -> "FOOBAR($0, $1)")
      Adapter.defineFunctionN('foobar2, 'all -> "$0 FOOBAR($2, $1)")

      name.foobar(10).partial.toPseudoSQL should be === "FOOBAR(examples.name, 10)"
      name.foobar2(10, 20).partial.toPseudoSQL should be === "examples.name FOOBAR(20, 10)"
    }

    "create a method that has different behaviour for each adapter" in {
      Adapter.defineFunctionN('foobar, 'all -> "$0 FOO($1)", 'oracle -> "$0 BAR($1)")

      Adapter configure 'mysql
      name.foobar(10).partial.toPseudoSQL should be === "examples.name FOO(10)"

      Adapter configure 'oracle
      name.foobar(10).partial.toPseudoSQL should be === "examples.name BAR(10)"
    }
  }
}
