package deepcopy

import dynamictail._


object deepcopy {
  def compileCopiers(copiers: List[Copier]): TotalCopier => TotalCopier = { tc =>
    val allCopiers: PartialFunction[Any, DynRec[Any]] =
      copiers.foldLeft(PartialFunction.empty[Any, DynRec[Any]]) {
        (accum, next) => accum.orElse(next(tc)) }

    x => if (allCopiers isDefinedAt x) allCopiers(x) else done(x)
  }

  def Y(f: TotalCopier => TotalCopier): TotalCopier =
    a => f(Y(f))(a)

  def apply[A](struct: A, cs: List[Copier] = copiers.all): A =
    Y(compileCopiers(cs))(struct).compute().asInstanceOf[A]

  case class Ping(var str: String, var pong: Pong)
  case class Pong(var owner: Ping)

  def main(args: Array[String]): Unit = {
    val ping  = Ping("foo", null)
    val pong1 = Pong(ping)
    ping.pong = pong1
    val pong2 = deepcopy(pong1)

    println(pong2)
  }
  // call(dc, x).map(Some(_))



  // Cont(Cont(Done(1),dc,Set(),Map()),Some(_),Set(),Map())
  // Cont(Done(1), (x: a /*1*/) => Cont(dc(x), Some(_), Set(x), Map()), Set(), Map())
  // Cont(Done(1), x => Done(Some(x)), Set(1), Map())

  // Done(1)
  // 1
}
