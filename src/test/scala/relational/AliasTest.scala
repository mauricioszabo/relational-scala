import org.scalatest._
import relational._

class AliasTest extends WordSpec with matchers.ShouldMatchers {
  val people = new tables.Table("people")
  val newPeople = people.as("p")

  "Alias in tables" should {
    "rename tables in SELECT clauses" in {
      newPeople.partial.toPseudoSQL should be === "\"people\" p"
    }

    "rename attributes" in {
      newPeople('id).partial.toPseudoSQL should be === "\"p\".\"id\""
    }

    "rename itself" in {
      newPeople.as("foo").partial.toPseudoSQL should be === "\"people\" foo"
    }
  }

  "Alias in attributes" should {
    "rename attribute" in {
      people('id).as("foo").selectPartial.toPseudoSQL should be === "\"people\".\"id\" foo"
      people('id).as("foo").partial.toPseudoSQL should be === "\"foo\""
    }

    "rename itself" in {
      people('id).as("foo").as("bar").partial.toPseudoSQL should be === "\"bar\""
    }
  }

  "Alias in select" should {
    val selector = new Selector(new clauses.Select(false, people, 'id), Seq(people))

    "rename queries" in {
      selector.as("sql").partial.toPseudoSQL should be === "(SELECT \"people\".\"id\" FROM \"people\") sql"
    }

    "rename attributes within those queries" in {
      val alias = selector.as("sql")
      alias('id).partial.toPseudoSQL should be === "\"sql\".\"id\""
    }
  }
}
