package test.relational.queries

import relational.queries._
import org.scalatest._

class ForComprehensionTest extends WordSpec with matchers.ShouldMatchers with BeforeAndAfterEach {
  implicit val adapter = new relational.Adapter('mysql)

  "Simple selects" should {
    "find some specific attributes" in {
      val result = for( user <- Table('people) ) yield Map('name -> user.get('name))
      sqlFor(result) should be === "SELECT `people`.`name` FROM `people`"
    }

    "filters results" in {
      val result = for {
        user <- Table('people)
        if(user.name == "Foo")
      } yield Map('name -> user.get('name))

      sqlFor(result) should be === "SELECT `people`.`name` FROM `people` " +
        "WHERE `people`.`name` = 'Foo'"
    }

    "select from two tables" in {
      val result = for {
        user <- Table('people)
        child <- Table('children)
        if(child.name == "Foo")
      } yield Map('age -> child.get('age), 'name -> user.get('name))

      sqlFor(result) should be === "SELECT `people`.`name`, `children`.`age` FROM `people`,`children` " +
        "WHERE `children`.`name` = 'Foo'"
    }

    "add a filter even if it's on different order" in {
      val result = for {
        user <- Table('people)
        child <- Table('children)
        if(user.name == "Foo")
      } yield Map('age -> child.get('age), 'name -> user.get('name))

      sqlFor(result) should be === "SELECT `people`.`name`, `children`.`age` FROM `people`,`children` " +
        "WHERE `people`.`name` = 'Foo'"
    }

    "add a filter for two tables" in {
      val result = for {
        user <- Table('people)
        if(user.name == "Foo")
        child <- Table('children)
        if(child.name == "Bar")
      } yield Map('age -> child.get('age))

      sqlFor(result) should be === "SELECT `children`.`age` FROM `people`,`children` " +
        "WHERE (`people`.`name` = 'Foo' AND `children`.`name` = 'Bar')"
    }
  }

  "Selects with Joins" should {
    "inner join a table, if a condition depends on it" in {
      val result = for {
        p <- Table('people)
        c <- Table('children)
        if(p.id == c.parent_id)
      } yield p.any('id)

      sqlFor(result) should be === "SELECT `people`.`id` FROM `people` INNER JOIN `children` " +
        "ON `people`.`id` = `children`.`parent_id`"
    }

    "left join a table" in {
      pending
      //val result = for {
      //  p <- Table('people)
      //  c <- LeftJoin('children, c => c.parent_id == p.id)
      //  if(c.name == "Foo")
      //} yield p.any('id)

      //sqlFor(result) should be === "SELECT `people`.`id` FROM `people` LEFT JOIN `children` " +
      //  "ON `children`.`parent_id` = `people`.`id` WHERE `children`.`name` = 'Foo'"
    }

    "inner join a table, with AND condition" in {
      val result = for {
        p <- Table('people)
        c <- Table('children)
        if(p.id == c.parent_id && p.name == "Foo")
      } yield p.any('id)

      sqlFor(result) should be === "SELECT `people`.`id` FROM `people` INNER JOIN `children` " +
        "ON `people`.`id` = `children`.`parent_id` WHERE `people`.`name` = 'Foo'"
    }
  }

  "When querying a query" should {
    val query = for( a <- Table('as); b <- Table('bs); if a.id == 10 ) yield (a.any('name), b.any('address))

    "create a restriction in the former query" in {
      val result = for {
        q <- query
        if q.as.name == "boo" && q.bs.id == 20
      } yield q.any('address)

      sqlFor(result) should be === "SELECT `bs`.`address` FROM `as`,`bs` WHERE (`as`.`id` = 10 AND " +
        "`as`.`name` = 'boo' AND `bs`.`id` = 20)"
    }

    "joins full queries with tables" in {
      val result = for {
        q <- query
        t <- Table('people)
        if q.as.name == "boo" && q.bs.id == 20 && t.name == "bar"
      } yield q.any('address)

      sqlFor(result) should be === "SELECT `bs`.`address` FROM `as`,`bs`,`people` WHERE (`as`.`id` = 10 AND " +
        "`as`.`name` = 'boo' AND `bs`.`id` = 20 AND `people`.`name` = 'bar')"
    }
  }

  def sqlFor(result: relational.Partial) = result.partial.toPseudoSQL
}
