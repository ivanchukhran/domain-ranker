import scala.concurrent.Future
import Data.DomainStats



case object Messages {
  case class Push(domainsStats: Future[List[DomainStats]])

  case class Pull(n_first: Int)
}
