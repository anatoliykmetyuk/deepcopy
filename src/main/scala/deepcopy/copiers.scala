package deepcopy

import scala.util.control.TailCalls.{ tailcall => call, _ }

object copiers {
  def all = List(option, list, product)

  def option: Copier = dc => {
    case Some(x) => call(dc(x)).map(Some(_))
    case None    => done(None)
  }

  def list: Copier = dc => {
    case x :: xs =>
      for {
        xc  <- call(dc(x ))
        xsc <- call(dc(xs))
      } yield xc :: xsc.asInstanceOf[List[Any]]

    case Nil => done(Nil)
  }

  def product: Copier = dc => { case x: Product =>
    val clazz       = x.getClass
    val constructor = clazz.getConstructors.head
    val parameters  = x.productIterator.toList

    call(dc(parameters)).map { params =>
      constructor.newInstance(params.asInstanceOf[List[Object]]: _*) }
  }
}
