import scala.util.control.TailCalls.{ tailcall => call, _ }

package object deepcopy {
  type TotalCopier = Any => TailRec[Any]
  type Copier      = TotalCopier => PartialFunction[Any, TailRec[Any]]
}
