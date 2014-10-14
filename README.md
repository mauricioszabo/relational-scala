Relational Scala
================

An API to query databases.

## Motivation
ORMs are quite interesting pieces of software: they can generate SQL in such a
way that it is secure and simple. But the simple truth is that they do not
solve every problem, and in most ways we need to generate SQL by hand.

The problem is the "gap"

When we generate SQL by hand, we lost most of the advantages that ORMs give us.
On the other hand, when we use the ORM, things "just work" but we lost all the
fine-tuning queries we can use.

## Enter Relational Scala
Using the power of Scala and flexibility of SQL, we can create a fine-tunned
query without losing power on the SQL way. Some tradeoffs need to be taken-we
never write SQL in string way, but we can do quite close using Scala's operator
overloading.

We don't ever use SQL Strings in Relational because we never really lose information
about where every part of SQL came from. For example, let's see the following
query (in SQL):

```sql
SELECT "users".*, "a"."road" FROM "users" INNER JOIN "addresses" "a" ON "a"."user_id" = "users"."id" WHERE "users"."name" LIKE 'Adam%' OR "users"."age" > 20
```

In Relational, this query is basically the following list of classes:

```scala
import relational._

val users = tables.Table("users")
val aliased = tables.Alias("a", tables.Table("users"))

val query = Selector(
  select=clauses.Select.select(attributes.AllInTable(users), attributes.Attribute(aliased, "road")),
  from=Seq(users),
  join=Seq(
    joins.InnerJoin(
      aliased, 
      comparissions.Equality(
        comparission.Equality.Equals, 
        attributes.Attribute(aliased, "user_id"), 
        attributes.Attribute(users, "id")
      )
    )
  ),
  where=comparissions.Or( Vector(
    comparissions.Equality(
      comparission.Equality.Like, 
      attributes.Attribute(users, "name"), 
      attributes.Literal("Adam%")
    ),
    comparissions.Equality(
      comparission.Equality.Gt, 
      attributes.Attribute(users, "age"), 
      attributes.Literal(20)
    )
))
)
```

Of course, the above way is far too verbose to be usable, but it presents a very interesting
concept: queries are formed by "fragments", or in Relational's terms, `Partial`s. A `Partial`
is a function that takes an `Adapter` and returns a `(String, Seq[Any])`. The  advantage
of this approach is that we never really lose SQL information: we know that **WHERE** clause
came from an `Or`, and inside this `Or` we have two `Equality` - one like, and one equal.

This advantage permits us to negate whole bunch of queries, or join them, with ease. Even
more, it makes it easy to identify malformed queries, or similar queries, and even to create
a query incrementally: we can just "add" more conditions into a where, or "add" more joins,
with ease.

##Simple Queries

Supose we want to find a list of people:

```scala
import relational._

object People extends Query {
}

People.where { p => p('name) like "Foo" }
People.where { p => p('id) < 10 || p('id) > 20 }

```

We can construct our queries using `select`, `group`, `having`, `where`,
`order`, `leftJoin`, `innerJoin` (or just `join`), `rightJoin`, `from`, etc.

```scala
//Finds all people that has someone with the same name in this table
People.
  select { p => p('name), p('name).count.as("number") }.
  where { p => p('age) > 18 }.
  group { p => p('name) }.
  having { p => p('name).count > 1 }
```

But this is tedious to do (all these blocks), so there is a better way:

```scala
People.query { p =>
  p.select (p('name), p('name).count.as("number") ).
  where (p('age) > 18).
  group (p('name)).
  having (p('name).count > 1)
}
```

Combining these two techiques, we can even use "implicits" to be more concise:

```scala
People.query { implicit p =>
  p.select ('name, 'name.count.as("number") ).
  where ('age > 18).
  group ('name).
  having ('name.count > 1)
}
```

## Joins
It is possible to make joins (even the most complex ones) using a simple
syntax: there are the methods `leftJoin`, `rightJoin`, and `join` (inner join).
Each of these perform a simple join and return a "JoinHelper" object, where you
call the `on` method and return the join condition.

```scala
People join 'addresses on { (p, a) => p('id) == a('person_id) }
//Constructs a query: SELECT * FROM people INNER JOIN addresses ON people.id = addresses.person_id
```

## Results
By default, each command prepares the query but never really tries to find
anything on database. So, you should call "results" to fetch the results of the
query. These will give you a List of Attributes, so you can convert these to
any format you desire:

```scala
val people = People query { implicit p =>
  p select ('name, 'name.count.as("number")) group 'number
}

people.foreach { p =>
  val name = p get 'name
  val count = p attribute 'number as Int
  println("There are " + count + " names '" + name "' on database")
}
```

Note that "count" is an `Int`, because of `as Int` part of the code.

Relational Scala doesn't tries to be "type-safe": indeed, SQL itself is not
"static typed": you can, for instance, cast "name" to an INTEGER on SQL, and
there is no way to catch this on Scala (or any other language).

## For Comprehensions

There is another way that we can create queries, which is using "for compreehensions".
It works for simple queries, and for more complex ones too. The idea is simple: in
Relational, we *know* when we're using a group-by condition, or a simple condition, or
what are tables, aliases, and such. So, we can create Inner Joins and Having conditions
using only for comprehensions:

```scala
import relational.queries._
import relational.functions._

for {
  user <- Table('users)
  if user.name == "Foo"
  address <- Table('addresses)
  if address.user_id == user.id
  child <- Table('children)
  if child.user_id == user.id && Count(child.id) > 1
} yield (user.get[String]('name), address.get[String], child.get[Int]('age))
```

The above code is able to generate a query for the following SQL:

```sql
SELECT "children"."age", "addresses"."road", "users"."name" 
FROM "users" 
INNER JOIN "children" ON "children"."user_id" = "users"."id" 
INNER JOIN "addresses" ON "addresses"."user_id" = "users"."id" 
WHERE "users"."name" = 'Foo' 
HAVING COUNT("children"."id") > 1
```

And, when run, will produce a `Stream[(String, String, Int)]` with the results from
the database.