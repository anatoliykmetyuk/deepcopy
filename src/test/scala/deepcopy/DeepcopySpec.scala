package deepcopy

import org.scalatest._


class DeepcopySpec extends FlatSpec with Matchers {
  import DeepcopySpecHelpers._

  "Deepcopy" should "work in case of flat classes" in {
    val b1 = Book("foo")
    val b2 = deepcopy(b1)
    b1 shouldBe b2
    b1.author = "bar"

    b1.author shouldBe "bar"
    b2.author shouldBe "foo"
  }

  it should "work in case of nested data structures" in {
    val b  = Book("foo")
    val l1 = List(List(List(Some(b), Some(2), None)))
    val l2 = deepcopy(l1)
    l1 shouldBe l2
    b.author = "bar"

    def lense(l: List[List[List[Option[Any]]]]) =
      l.head.head.head.get.asInstanceOf[Book].author

    lense(l1) shouldBe "bar"
    lense(l2) shouldBe "foo"
  }

  it should "work with sequence arguments" in {
    val b  = Book("foo")
    val a1 = Author(List(b))
    val a2 = deepcopy(a1)
    a1 shouldBe a2
    b.author = "bar"

    a1.books.head.author shouldBe "bar"
    a2.books.head.author shouldBe "foo" 
  }

  it should "work with options" in {
    val b  = Book("foo")
    val a1 = MaybeAuthor(Some(b))
    val a2 = deepcopy(a1)
    a1 shouldBe a2
    b.author = "bar"

    a1.book.get.author shouldBe "bar"
    a2.book.get.author shouldBe "foo"
  }

  it should "work in case of deep nesting" in {
    val c = C("foo")
    val a1 = A(B(c))
    val a2 = deepcopy(a1)
    a1 shouldBe a2
    a1.b.c.value = "bar"

    a1.b.c.value shouldBe "bar"
    a2.b.c.value shouldBe "foo"
  }

  it should "work in case of recursive trees" in {
    val n = Num(10)
    val e1 = n + n + n + n
    val e2 = deepcopy(e1)
    e1 shouldBe e2
    n.n = 20

    def lense(e: Expr): Num = e.add.left.add.left.add.left.num

    lense(e1).n shouldBe 20
    lense(e2).n shouldBe 10
  }
}

object DeepcopySpecHelpers {
  case class Book(var author: String)
  case class Author(var books: List[Book])
  case class MaybeAuthor(var book: Option[Book])

  case class C(var value: String)
  case class B(var c: C)
  case class A(var b: B)

  sealed trait Expr {
    def +(that: Expr): Expr = Add(this, that)
    def add: Add = this.asInstanceOf[Add]
    def num: Num = this.asInstanceOf[Num]
  }
  case class Num(var n: Int) extends Expr
  case class Add(var left: Expr, var right: Expr) extends Expr
}
