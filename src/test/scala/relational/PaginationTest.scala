import org.scalatest._
import relational._

class PaginationTest extends WordSpec with matchers.ShouldMatchers {
  val sql = PartialStatement { a => "SELECT * FROM foo" -> Nil }

  "OFFSET" should {
    "skip records on a select" in {
      val p = new Pagination(sql, offset=2)
      p.partial.toPseudoSQL should be === "SELECT * FROM foo OFFSET 2"
    }
  }

  "LIMIT" should {
    "limit the search for a maximum of N results" in {
      val p = new Pagination(sql, limit=2)
      p.partial.toPseudoSQL should be === "SELECT * FROM foo LIMIT 2"
    }
  }
}
