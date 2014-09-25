package test.relational.joins

import org.scalatest._
import relational.joins._
import relational.tables.Table


class JoinTest extends WordSpec with matchers.ShouldMatchers {
  val table = new Table("examples")
  val joined = new Table("joined")

  "Join" should {
    "extract join information" in {
      val left = new LeftJoin(joined, table.id == joined.e_id)
      val inner = new InnerJoin(joined, table.id2 == joined.e_id2)

      val Join(table1, condition1, kind1) = left
      table1 equivalentTo joined should be (true)
      condition1 equivalentTo (table.id == joined.e_id) should be (true)
      kind1 should be === 'left

      val Join(_, condition2, kind2) = inner
      condition2 equivalentTo (table.id2 == joined.e_id2) should be (true)
      kind2 should be === 'inner
    }
  }
}
