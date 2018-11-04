package deepcopy

object copiers {
  val optionCopier: Copier = dc => {
    case Some(x) => call(dc, x).map(Some(_))
    case None    => pure(None)
  }

  val listCopier: Copier = dc => { case x :: xs =>
    for {
      xc  <- call(dc, x  )
      xsc <- call(dc, xsc)
    } yield xc :: xsc
  }

  val productCopier: Copier = dc => { case x: Product =>
    val clazz       = x.getClass
    val constructor = clazz.getConstructors.head
    val parameters  = x.productIterator.toList

    call(dc, parameters) >>= constructor.newInstance(_: _*)
  }
}
