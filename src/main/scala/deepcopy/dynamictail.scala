package deepcopy

import annotation.tailrec


object dynamictail {
  
  sealed trait Dynamic[A] {
    def step: Either[Dynamic[A], A] = this match {
      case Done(a) => Right(a)
      case Cont(Done(a), f, stack, mem) =>
        val mem2 = stack.foldLeft(mem) { (m, next) => m.updated(next, a) }
        mem2.get(f) match {
          case Some(x) => Done(x)
          case None => f(a) match {
            case c: Cont => c.copy(mem = c.mem + mem2)
            case d: Done => d
          }
        }

      case Cont(Cont(d: Dynamic[a], f, Set.empty, Map.empty), ff, stack, mem) =>
        Cont(d, x => Cont(f(x), ff, stack + x, mem)) 
    }

    @tailrec final def compute(): A = step match {
      case Right(a) => a
      case Left (p) => p.compute()
    }

    def flatMap[B](f: A => Dynamic[B]): Dynamic[B] = dynamictail.flatMap(this)(f)
    def map    [B](f: A => B         ): Dynamic[B] = dynamictail.map    (this)(f)
    def >>=    [B](f: A => Dynamic[B]): Dynamic[B] = flatMap(f)
  }

  final private[this] case class Done[A](a: A) extends Dynamic[A]
  final private[this] case class Cont[A, B](d: Dynamic[A], f: A => Dynamic[B]
    , stack: Set[Any] = Set.empty, memory: Map[Any, Any] = Map.empty) extends Dynamic[B]

  def done   [A   ](a: A                              ): Dynamic[A] = Done(a)
  def flatMap[A, B](da: Dynamic[A])(f: A => Dynamic[B]): Dynamic[B] = Cont(da, f)
  def call   [A, B](f: A => Dynamic[B], a: A          ): Dynamic[B] = flatMap(done(a))(f)
  def map    [A, B](pa: Dynamic[A])(f: A => B         ): Dynamic[B] = flatMap(pa)(a => done(f(a)))
}
