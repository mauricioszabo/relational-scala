Mapper
======

Mapper is an object-relational mapper using Relational Scala. The objective is
to map a database using DataMapper pattern.

## Why DataMapper?
Althrough the ActiveRecord pattern is more easy to use, DataMapper is more
flexible and robust solution, and in big systems we have needs that are not
easily fitted on ActiveRecord. Furthermore, in Scala we have the separation in
*Classes* and *Objects*. So, it's really more easy to map the "Objects" into the
Mappers, and the "Classes" into Domain Objects.

## How it works?
To begin simple, imagine we have a "people" table on some database. First of
all, we need a domain object that represents this element. We can do this with
any object, but for simplicity let's create a class with the ORM's structure and
inherits from `Mapping`.

`Mapping` will try to create "properties", "validations", and will automatically
map (and try to cast) these attributes to from the database to the Domain
Object.

```scala
class Person extends Mapping {
  val name = attr.name.as[Property, String]
  val age = attr.age.as[Property, Int]
  val email = attr.email.as[Property, String]
}
```

To be able to communicate with the Database, we should create a `Mapper`, a
object that automatically maps objects to database. Note that we need to pass
the Domain Object to the mapper, so it can map automatically to our object:

```scala
object People extends Mapper[Person]

val foos = People where { p => p('name) == "Foo Bar" }
foos.foreach { person => 
  println(person.name.value + " -> " + person.age.value)
}

// this finds all users where age is > 18 and name is "Foo Bar"
val adults = foos.where { p => p('age) > 18 && foos.where }
```

The above code will try to find records on database, using the syntax on
Relational Scala, and automatically map then to the Person object. It will use
the `mappings` field on Person to do these mappings, so you can initialize a
"Person" to default fields if you override `mappings` (which is an `Map[Symbol, Any]` object):

```scala
class Person(a: (Symbol, Any)* ) extends Mapping {
  override val mappings = Map(a: _*)
  
  val name = attr.name.as[Property, String]
  val age = attr.age.as[Property, Int]
  val email = attr.email.as[Property, String]
}

val me = new Person('name -> "Me", 'age -> 20)
println(me.name.value) // => Me
println(me.age.value) // => 20
println(me.email.value) // => null
println(me.email.isPresent) // => false

val other = new Person('age -> "String")
println(me.age.value) // => 0
println(me.age.isPresent) // => false
```

Please note that if you have any code that needs to run on the constructor that
depends on the `mappings` on depends on any `Property`, it'll not receive the
right values when it's being populated by the `Mapper`, so you'll need to create
your own mapper for that.

### How Mapper works
`Mapper` mixes various behaviours. 

* It mixes `IdentityMap`, that has a abstract method `mapTo`. This method receives the Relational's `Attributes` from the Database, and tries to map to another object. 
* It mixes Relational's `QueryBase` to be able to make queries as Relational's `Query` do
* It mixes `Finders` to use `finders.define` and to use `find([primary_key])`

### Manually mapping from the Mapper to the Object
If you need to create the objects in a more sensible manner, you can use `IdentityMap`'s `mapTo[A](Attributes): A` to create the object in the way you want:

```scala
case class Address(road: String, number: Int)

object Addresses extends Mapper[Address] {
  table = "people_addresses"
  
  override def mapTo(attributes: Attributes) = {
    val road = attributes.get('road_name)
    val number = attributes attribute 'number as Int
    Address(road, number)
  }
}

val nemos = Addresses.where { a => a('road) like "P. Sherman%" }
nemos.foreach { address => println(address.road) }
```
