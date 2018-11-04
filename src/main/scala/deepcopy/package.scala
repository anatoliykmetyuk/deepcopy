package object deepcopy {
  type TotalCopier = Any => Deepcopy[Any]
  type Copier      = TotalCopier => PartialFunction[Any, Deepcopy[Any]]
}
