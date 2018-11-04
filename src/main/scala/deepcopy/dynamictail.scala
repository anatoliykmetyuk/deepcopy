package deepcopy

import annotation.tailrec


object dynamictail {
  type CallSig = (Any, Function[_, _])

  sealed trait DynRec[A] {
    def step: Either[DynRec[A], A] = this match {
      case Done(a) => Right(a)
      case Cont(Done(a), f, stack, mem) =>
        val mem2 = stack.foldLeft(mem) { (m, next) => m.updated(next, a) }
        Left(mem2.get((a, f)) match {
          case Some(x) => Done(x.asInstanceOf[A])
          case None => f(a) match {
            case c: Cont[_, A] => c.copy(memory = c.memory ++ mem2)
            case d: Done[A] => d
          }
        })

      case Cont(Cont(Done(a), f, _, _), ff, stack, mem) if mem.isDefinedAt((a, f)) =>
        Left(Cont(Done(mem((a, f))), ff, stack, mem))

      case Cont(Cont(d: DynRec[a], f, s, m), ff, stack, mem) if s.isEmpty && m.isEmpty =>
        Left(Cont(d, (x: a) => Cont(f(x), ff, stack + ((x, f)), Map.empty), Set.empty, mem))
    }

    @tailrec final def compute(): A = {
      println(this)
      step match {
        case Right(a) => a
        case Left (p) => p.compute()
      }
    }

    def flatMap[B](f: A => DynRec[B]): DynRec[B] = dynamictail.flatMap(this)(f)
    def map    [B](f: A => B        ): DynRec[B] = dynamictail.map    (this)(f)

    def as[B]: DynRec[B] = this.asInstanceOf[DynRec[B]]
  }

  case class Done[A](a: A) extends DynRec[A]
  case class Cont[A, B](d: DynRec[A], f: A => DynRec[B]
    , stack: Set[CallSig], memory: Map[CallSig, Any]) extends DynRec[B]

  def done   [A   ](a: A                            ): DynRec[A] = Done(a)
  def flatMap[A, B](da: DynRec[A])(f: A => DynRec[B]): DynRec[B] = Cont(da, f, Set.empty, Map.empty)
  def call   [A, B](f: A => DynRec[B], a: A         ): DynRec[B] = flatMap(done(a))(f)
  def map    [A, B](pa: DynRec[A])(f: A => B        ): DynRec[B] = flatMap(pa)(a => done(f(a)))
}
