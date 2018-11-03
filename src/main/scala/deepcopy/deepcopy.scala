package deepcopy

object util {
  sealed trait Deepcopy[A]
  case class
}

object deepcopy {
  type TotalCopier = Any => Deepcopy[Any]
  type Copier      = TotalCopier => PartialFunction[Any, Deepcopy[Any]]

  def compileCopiers(dc: TotalCopier, copiers: List[Copier]): TotalCopier = {
    val allCopiers = copiers.foldLeft({case _: Nothing => ()}: Copier) { (accum, next) =>
      accum.orElse(next) }

    x => if (x.isDefinedAt(allCopiers)) allCopiers(x) else pure(x)
  }

  def deepcopy(copiers: List[Copier]): TotalCopier => TotalCopier =
    dc => x => compileCopiers(dc, copiers)(x)

  def Y(f: TotalCopier => TotalCopier): TotalCopier =
    a => f(Y(f))(a)

  def apply[A](struct: A, copiers: List[Copier] = copiers.all): A =
    Y(deepcopy(copiers))(struct).run
}

object copiers {
  val optionCopier: Copier = dc => {
    case Some(x) => call(dc(x)).map(Some(_))
    case None    => pure(None)
  }

  val listCopier: Copier = dc => { case x :: xs =>
    for {
      xc  <- call(dc(x  ))
      xsc <- call(dc(xsc))
    } yield xc :: xsc
  }

  val productCopier: Copier = dc => { case x: Product =>
    val clazz       = x.getClass
    val constructor = clazz.getConstructors.head
    val parameters  = x.productIterator.toList

    call(dc(parameters)) >>= constructor.newInstance(_: _*)
  }
}
