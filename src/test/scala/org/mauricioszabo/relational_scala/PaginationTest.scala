import org.scalatest._
import org.mauricioszabo.relational_scala._

class PaginationTest extends WordSpec with matchers.ShouldMatchers {

  "OFFSET" should {
    "skip records on a select" in {
      val p = new Pagination("SELECT * FROM foo", Nil, offset=2)
      p.partial.toPseudoSQL should be === "SELECT * FROM foo OFFSET 2"
    }
  }

  "LIMIT" should {
    "limit the search for a maximum of N results" in {
      val p = new Pagination("SELECT * FROM foo", Nil, limit=2)
      p.partial.toPseudoSQL should be === "SELECT * FROM foo LIMIT 2"
    }
  }
}
