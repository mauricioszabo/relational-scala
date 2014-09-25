package test.relational.functions

import org.scalatest._
import relational.functions._

class SqlFunctionsTest extends WordSpec with test.relational.Helpers {
  "SQL Functions" should {
    "support group by functions (SUM, AVERAGE, MAX, MIN, COUNT)" in {
      pseudoSQL(Sum(name) == 20) should be === """SUM("examples"."name") = 20"""
      pseudoSQL(Avg(name) == 15) should be === """AVG("examples"."name") = 15"""
      pseudoSQL(Max(name) == 15) should be === """MAX("examples"."name") = 15"""
      pseudoSQL(Min(name) == 15) should be === """MIN("examples"."name") = 15"""
      pseudoSQL(Count(name) == 15) should be === """COUNT("examples"."name") = 15"""
      pseudoSQL(CountDistinct(name) == 15) should be === """COUNT(DISTINCT "examples"."name") = 15"""
    }

    "support non group by functions (LENGTH, UPPER, LOWER)" in {
      pseudoSQL(Length(name) == 1) should be === """LENGTH("examples"."name") = 1"""
      pseudoSQL(Upper(name) == "UP") should be === """UPPER("examples"."name") = 'UP'"""
      pseudoSQL(Lower(name) == "up") should be === """LOWER("examples"."name") = 'up'"""
    }
  }

  "Aggregation SQL functions" should {
    "display the attribute they're being applied" in {
      val grouped: Aggregation = Sum(name).asInstanceOf[Aggregation]
      pseudoSQL(grouped.aggregated) should be === """"examples"."name""""
    }
  }
}
