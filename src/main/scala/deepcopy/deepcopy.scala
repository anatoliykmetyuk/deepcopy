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

  def apply[A](struct: A, cs: List[Copier] = copiers.all): Any =
    Y(compileCopiers(cs))(struct).compute()

  case class Book(var author: String, var price: Int)

  def main(args: Array[String]): Unit = {
    val b1 = Some(1)
    val b2 = apply(b1)
    println(b2)
    // println(Cont(Done(1), (x: Int) => Done(Some(x)), Set((1, null)), Map()).compute())
  }
  // call(dc, x).map(Some(_))



  // Cont(Cont(Done(1),dc,Set(),Map()),Some(_),Set(),Map())
  // Cont(Done(1), (x: a /*1*/) => Cont(dc(x), Some(_), Set(x), Map()), Set(), Map())
  // Cont(Done(1), x => Done(Some(x)), Set(1), Map())

  // Done(1)
  // 1
}
