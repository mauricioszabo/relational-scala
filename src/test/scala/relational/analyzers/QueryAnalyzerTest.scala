package test.relational.analyzers

import org.scalatest._
import relational.tables.Table
import relational.attributes.Attribute
import relational.joins._
import relational.analyzers.QueryAnalyzer
import relational.Selector
import relational.clauses.Select

class QueryAnalyzerTest extends WordSpec with matchers.ShouldMatchers {
  val ex = new Table("examples")
  val joined = new Table("joined")
  val aux = new Table("auxiliary")

  "QueryAnalyzer" should {
    "show which columns are the same in the query" in {
      val selector = Selector(
        select=Select.select(ex.id, joined.ex_id, ex.name, ex.alt_name, ex.obj, ex.obj2, ex.single),
        from=List(ex),
        join=List(InnerJoin(joined, ex.id == joined.ex_id)),
        where=(ex.name == ex.alt_name),
        having=(ex.obj2 == ex.obj)
      )
      val analyzer = new QueryAnalyzer(selector)

      analyzer.fields should be === Set(
        Set(ex.id, joined.ex_id),
        Set(ex.name, ex.alt_name),
        Set(ex.obj, ex.obj2),
        Set(ex.single)
      )
    }
  }
}
