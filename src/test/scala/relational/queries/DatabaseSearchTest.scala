package test.relational.queries

import relational.Adapter
import relational.queries._
import org.scalatest._
import relational.attributes.AttributeLike
import relational.PartialStatement
import java.sql._

class DatabaseSearchTest extends WordSpec with tests.DatabaseSetup with matchers.ShouldMatchers {
  "Simple queries" should {
    "generate a simple list" in {
      val query = for {
        p <- Table('scala_people)
        if p.name == "Foo"
      } yield Map('id -> p.any('id), 'age -> p.any('age))

      query.asStream(createStatement).toSet should be === Set(
        Map('id -> 1, 'age -> 17),
        Map('id -> 2, 'age -> 18)
      )
    }

    "returns an option if casting information is unknown" in {
      globalConnection.prepareStatement(
        "INSERT INTO scala_people VALUES(4, NULL, 18, '2010-10-10 08:00')").executeUpdate
      val query = for(p <- Table('scala_people) if p.age == 18) yield p.maybe[String]('name)

      query.asStream(createStatement).toSet should be === Set(Some("Foo"), None)
    }
  }

  def createStatement(partial: PartialStatement) = {
    val statement = globalConnection.prepareStatement(partial.sql(Adapter))
    setParams(statement, partial.attributes)
    statement.executeQuery
  }

  private def setParams(statement: PreparedStatement, attributes: Seq[Any]) =
  1 to attributes.size foreach { i =>
    attributes(i-1) match {
      case int: Int => statement.setObject(i, int)
      case str: String => statement.setObject(i, str)
    }
  }
}
