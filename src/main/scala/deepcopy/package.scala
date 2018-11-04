package object deepcopy {
  import dynamictail._

  type TotalCopier = Any => DynRec[Any]
  type Copier      = TotalCopier => PartialFunction[Any, DynRec[Any]]
}
