Deep copy for mutable Scala structures.

# Usage
Add the dependency to your your `build.sbt`:

```scala
libraryDependencies += "com.functortech" %% "deepcopy" % "0.1.0"
```

Then, use it as follows:

```scala
scala> import deepcopy._
import deepcopy._

scala> case class Author(var x: String)
defined class Author

scala> case class Book(authors: List[Author])
defined class Book

scala> val author = Author("foo")
author: Author = Author(foo)

scala> val book1 = Book(List(author))
book1: Book = Book(List(Author(foo)))

scala> val book2 = deepcopy(book1)
book2: Book = Book(List(Author(foo)))

scala> author.x = "bar"
author.x: String = bar

scala> book1
res1: Book = Book(List(Author(bar)))

scala> book2
res2: Book = Book(List(Author(foo)))
```

You can find more examples at the test suits.

Currently it deeply copies only `List`s, `Option`s and case classes. You can override the default copying logic. To do so, please have a look at the `deepcopy.apply` method (specifically its `cs` argument that specifies copiers). Use the default copiers defined under `copiers.scala` as a reference to implement your own copiers.
