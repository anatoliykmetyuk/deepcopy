package deepcopy

import cats._, cats.implicits._
import dynamictail._

import java.lang.reflect.Method


object copiers {
  def all = List(option, list, product)

  def option: Copier = dc => {
    case Some(x) => call(dc, x).map(Some(_))
    case None    => done(None)
  }

  def list: Copier = dc => {
    case x :: xs =>
      for {
        xc  <- call(dc, x )
        xsc <- call(dc, xs)
      } yield xc :: xsc.asInstanceOf[List[Any]]

    case Nil => done(Nil)
  }

  def product: Copier = dc => { case x: Product =>
    val clazz       = x.getClass
    val constructor = clazz.getConstructors.head
    val parameters  = x.productIterator.toList.asInstanceOf[List[Object]]
    
    def copyParams(ref: Any): DynRec[Unit] = {
      val setters = clazz.getDeclaredMethods.filter(_.getName.endsWith("_$eq")).toList
      val gettersAndSetters: List[(Method, Method)] = setters.flatMap(setter =>
        clazz.getDeclaredMethods.find { _.getName == setter.getName.reverse.drop(4).reverse }
          .map { getter => setter -> getter })

      gettersAndSetters.traverse { case (setter, getter) =>
        val original = getter.invoke(ref)
        dc(original).map { copied => setter.invoke(ref, copied.asInstanceOf[Object]) }
      } >> ().pure[DynRec]
    }

    for {
      reference <- doneFor((x, dc))(constructor.newInstance(parameters: _*))
      _         <- copyParams(reference)
    } yield reference
  }
}
