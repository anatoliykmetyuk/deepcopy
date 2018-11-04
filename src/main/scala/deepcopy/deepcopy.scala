package deepcopy

object deepcopy {
  def compileCopiers(dc: TotalCopier, copiers: List[Copier]): TotalCopier = {
    val allCopiers = copiers.foldLeft({case _: Nothing => ()}: Copier) { (accum, next) =>
      accum.orElse(next) }

    x => if (x.isDefinedAt(allCopiers)) allCopiers(x) else pure(x)
  }

  def Y(f: TotalCopier => TotalCopier): TotalCopier =
    a => f(Y(f))(a)

  def apply[A](struct: A, copiers: List[Copier] = copiers.all): A =
    Y(compileCopiers(_, copiers))(struct).run.asInstanceOf[A]
}
