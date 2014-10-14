package test.relational.queries

import relational.queries._
import org.scalatest._
import relational.functions._


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

      sqlFor(result) should be === "SELECT `children`.`age`, `people`.`name` FROM `people`, `children` " +
        "WHERE `children`.`name` = 'Foo'"
    }

    "add a filter even if it's on different order" in {
      val result = for {
        user <- Table('people)
        child <- Table('children)
        if(user.name == "Foo")
      } yield Map('age -> child.get('age), 'name -> user.get('name))

      sqlFor(result) should be === "SELECT `children`.`age`, `people`.`name` FROM `people`, `children` " +
        "WHERE `people`.`name` = 'Foo'"
    }

    "add a filter for two tables" in {
      val result = for {
        user <- Table('people)
        if(user.name == "Foo")
        child <- Table('children)
        if(child.name == "Bar")
      } yield Map('age -> child.any('age))

      sqlFor(result) should be === "SELECT `children`.`age` FROM `people`, `children` " +
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

  "Querying with aggregate functions" should {
    "adds the non-grouped-clauses on group by" in {
      val result = for( p <- Table('people) ) yield p.get(Count, 'name)
      sqlFor(result) should be === "SELECT COUNT(`people`.`name`) `count_people_name_` FROM `people`"

      val grouped = for( p <- Table('people) ) yield p.any('name) -> p.get(Count, p.name)
      sqlFor(grouped) should be === "SELECT `people`.`name`, COUNT(`people`.`name`) `count_people_name_` " +
        "FROM `people` GROUP BY `people`.`name`"
    }

    "add the aggregated on group by when referenced by a join or where" in {
      val result = for {
        p <- Table('people)
        j <- Table('joined)
        if j.person_id == p.id
      } yield (j.any('person_id), p.get(Count, 'id))

      sqlFor(result) should be === "SELECT `joined`.`person_id`, COUNT(`people`.`id`) `count_people_id_` " +
        "FROM `people` INNER JOIN `joined` ON `joined`.`person_id` = `people`.`id` " +
        "GROUP BY `joined`.`person_id`"
    }

    "add the aggregated conditions on HAVING" in {
      val result = for {
        p <- Table('people)
        if Count(p.name) > 2
      } yield p any 'name

      sqlFor(result) should be === "SELECT `people`.`name` FROM `people` HAVING COUNT(`people`.`name`) > 2"
    }

    "WOO!!!" should {
      //val criticalComponents = for( cc <- Table('critical_components) if cc.id < 4000) yield cc.any('id)

      //val analyzedJoin = for {
      //  analyzed <- criticalComponents.as("analyzed")
      //  criticalFromAnalyzed <- Table('critical_comp_criticidades)
      //  if analyzed.id == criticalFromAnalyzed.criticidade_id
      //} yield analyzed any 'id

      //val criticalities = for {
      //  //analyzed join

      //  critical <- Table('critical_comp_criticidades).as("ccc2")
      //  if analyzed.id == critical.criticidade_id
      //  if CountDistinct(analyzed.id) == Count(critical.criticidade_id)
      //} yield critical any 'criticidade_id

      //println(criticalities.partial.tuple(relational.Adapter))

      //println(sqlFor(criticalities))

      //Class.forName("org.postgresql.Driver");
      //val globalConnection = java.sql.DriverManager.getConnection("jdbc:postgresql:randon_development", "postgres", "")
      //def createStatement(partial: relational.PartialStatement) = {
      //  val (query, attributes) = partial.tuple(relational.Adapter)
      //  val statement = globalConnection.prepareStatement(query)
      //  setParams(statement, attributes)
      //  statement.executeQuery
      //}

      //def setParams(statement: java.sql.PreparedStatement, attributes: Seq[Any]) =
      //1 to attributes.size foreach { i =>
      //  attributes(i-1) match {
      //    case int: Int => statement.setObject(i, int)
      //    case str: String => statement.setObject(i, str)
      //  }
      //}

      //val res = criticalities.asStream(createStatement)
      //println(res)
    }
  }

  "When querying a query" should {
    val query = for( a <- Table('as); b <- Table('bs); if a.id == 10 ) yield (a.any('name), b.any('address))

    "create a restriction in the former query" in {
      val result = for {
        q <- query
        if q.as.name == "boo" && q.bs.id == 20
      } yield q.any('address)

      sqlFor(result) should be === "SELECT `bs`.`address` FROM `as`, `bs` WHERE (`as`.`id` = 10 AND " +
        "`as`.`name` = 'boo' AND `bs`.`id` = 20)"
    }

    "query on full queries with tables" in {
      val result = for {
        q <- query
        t <- Table('people)
        if q.as.name == "boo" && q.bs.id == 20 && t.name == "bar"
      } yield q.any('address)

      sqlFor(result) should be === "SELECT `bs`.`address` FROM `as`, `bs`, `people` WHERE (`as`.`id` = 10 AND " +
        "`as`.`name` = 'boo' AND `bs`.`id` = 20 AND `people`.`name` = 'bar')"
    }

    "join on queries with tables and full queries" in {
      val result = for {
        t <- Table('people)
        q <- query
        if t.id == q.as.people_id && q.as.id == q.bs.as_id
      } yield q.any('address)

      sqlFor(result) should be === "SELECT `bs`.`address` FROM `people` " +
        "INNER JOIN `as` ON `people`.`id` = `as`.`people_id` " +
        "INNER JOIN `bs` ON `as`.`id` = `bs`.`as_id` WHERE `as`.`id` = 10"
    }

    "add more fields in the subsequent select" in {
      val result = for {
        q <- query
        if q.as.name == "boo"
      } yield q.as.any('age)

      sqlFor(result) should be === "SELECT `as`.`age` FROM `as`, `bs` WHERE (`as`.`id` = 10 AND " +
        "`as`.`name` = 'boo')"
    }

    "query for grouping functions" in {
      val result = for(q <- query) yield (q.as any 'age, q.get(Count, q.as.age))

      sqlFor(result) should be === "SELECT `as`.`age`, COUNT(`as`.`age`) `count_as_age_` FROM `as`, `bs` " +
        "WHERE `as`.`id` = 10 GROUP BY `as`.`age`"
    }
  }

  "Aliasing tables and queries" should {
    "alias a table" in {
      val result = for {
        t <- Table('people).as("p")
        if t.id == 10
      } yield t any 'name

      sqlFor(result) should be === "SELECT `p`.`name` FROM `people` `p` WHERE `p`.`id` = 10"
    }

    "Alias a query" in {
      val query = for( a <- Table('as) if a.id == 10 ) yield a.any('name)
      val result = for( a <- query.as("q1") if a.name == "Foo") yield a any 'id

      sqlFor(result) should be === "SELECT `q1`.`id` FROM (SELECT `as`.`name` FROM `as` " +
        "WHERE `as`.`id` = 10) `q1` WHERE `q1`.`name` = 'Foo'"
    }
  }

  def sqlFor(result: relational.Partial) = result.partial.toPseudoSQL
}
