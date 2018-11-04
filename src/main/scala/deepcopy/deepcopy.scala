package deepcopy

import scala.util.control.TailCalls.{ tailcall => call, _ }


object deepcopy {
  def compileCopiers(copiers: List[Copier]): TotalCopier => TotalCopier = { tc =>
    val allCopiers: PartialFunction[Any, TailRec[Any]] =
      copiers.foldLeft(PartialFunction.empty[Any, TailRec[Any]]) {
        (accum, next) => accum.orElse(next(tc)) }

    x => if (allCopiers isDefinedAt x) allCopiers(x) else done(x)
  }

  def Y(f: TotalCopier => TotalCopier): TotalCopier =
    a => f(Y(f))(a)

  def apply[A](struct: A, cs: List[Copier] = copiers.all): A =
    Y(compileCopiers(cs))(struct).result.asInstanceOf[A]
}
