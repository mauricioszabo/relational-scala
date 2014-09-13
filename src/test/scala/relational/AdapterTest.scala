package test.relational

import org.scalatest._
import relational.{attributes => attrs}
import relational._

class AdapterTest extends WordSpec with matchers.ShouldMatchers with BeforeAndAfterEach {
  implicit val adapter = new Adapter
  adapter.configure('all)

  lazy val table = new tables.Table("examples")

  class Test extends attrs.Attribute(table, "name") {
    def foobar(param: Any) =
      new attrs.Function('foobar, this, attrs.Attribute.wrap(param))

    def foobar2(p1: Any, p2: Any) =
      new attrs.Function('foobar2, this, attrs.Attribute.wrap(p1), attrs.Attribute.wrap(p2))
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

      name.foobar(10).partial.toPseudoSQL should be === "FOO_BAR(\"examples\".\"name\", 10)"
    }

    "create a function in a simpler way" in {
      Adapter.defineFunctionN('foobar, 'all -> "FOOBAR($0, $1)")
      Adapter.defineFunctionN('foobar2, 'all -> "$0 FOOBAR($2, $1)")

      name.foobar(10).partial.toPseudoSQL should be === "FOOBAR(\"examples\".\"name\", 10)"
      name.foobar2(10, 20).partial.toPseudoSQL should be === "\"examples\".\"name\" FOOBAR(20, 10)"
    }

    "create a method that has different behaviour for each adapter" in {
      Adapter.defineFunctionN('foobar, 'all -> "$0 FOO($1)", 'oracle -> "$0 BAR($1)")

      adapter configure 'mysql
      name.foobar(10).partial.toPseudoSQL should be === "\"examples\".\"name\" FOO(10)"

      adapter configure 'oracle
      name.foobar(10).partial.toPseudoSQL should be === "\"examples\".\"name\" BAR(10)"
    }
  }
}
