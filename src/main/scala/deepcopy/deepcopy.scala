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
}
