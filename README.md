    Relational Scala
================

An API to query databases.

## Motivation
ORMs are quite interesting pieces of software: they can generate SQL in such a way that it is secure and simple. But the simple truth is that they do not solve every problem, and in most ways we need to generate SQL by hand.

The problem is the "gap"

When we generate SQL by hand, we lost most of the advantages that ORMs give us. On the other hand, when we use the ORM, things "just work" but we lost all the fine-tuning queries we can use.

## Enter Relational Scala
Using the power of Scala and flexibility of SQL, we can create a fine-tunned query without losing power on the SQL way. Some tradeoffs need to be taken-we never write SQL in string way, but we can do quite close using Scala's operator overloading.

##Simple Queries

Supose we want to find a list of people:

```scala
object People extends Query {
}

People.where { p => p('name) like "Foo" }
People.where { p => p('id) < 10 || p('id) > 20 }

```

We can construct our queries using `select`, `group`, `having`, `where`, `order`, `leftJoin`, `innerJoin` (or just `join`), `rightJoin`, `from`, etc.

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
It is possible to make joins (even the most complex ones) using a simple syntax: there are the methods `leftJoin`, `rightJoin`, and `join` (inner join). Each of these perform a simple join and return a "JoinHelper" object, where you call the `on` method and return the join condition.

```scala
People join 'addresses on { (p, a) => p('id) == a('person_id) }
//Constructs a query: SELECT * FROM people INNER JOIN addresses ON people.id = addresses.person_id
```

## Results
By default, each command prepares the query but never really tries to find anything on database. So, you should call "results" to fetch the results of the query. These will give you a List of Attributes, so you can convert these to any format you desire:

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

Relational Scala doesn't tries to be "type-safe": indeed, SQL itself is not "static typed": you can, for instance, cast "name" to an INTEGER on SQL, and there is no way to catch this on Scala (or any other language).