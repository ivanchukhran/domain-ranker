import akka.actor._
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.HttpCookie
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.{ExecutionContextExecutor, Future, Promise}
import scala.util.{Failure, Success}

object Requests {
  implicit val system: ActorSystem = ActorSystem("data-fetcher")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val connectionPool = Http().superPool[Promise[HttpResponse]]()

  def singleRequest(url: String, headers: List[HttpHeader] = Nil): Future[String] = {
    Source.single((HttpRequest(uri = url, headers = headers), Promise[HttpResponse]()))
      .via(connectionPool)
      .mapAsync(1) {
        case (Success(resp), p) =>
          resp.entity.dataBytes
            .runFold(ByteString.empty)(_ ++ _)
            .map(_.utf8String)
            .andThen { case _ => p.success(resp) }
        case (Failure(e), p) =>
          p.failure(e)
          Future.failed(e)
      }.toMat(Sink.head)(Keep.right).run()
  }

  def parallelRequests(urls: List[String],
                       headers: List[HttpHeader] = Nil,
                       parallelism: Int = 5): Future[List[String]] = {
    Source.fromIterator(() => urls.iterator)
      .mapAsync(parallelism)(url => singleRequest(url, headers))
      .toMat(Sink.seq)(Keep.right)
      .run()
      .map(_.toList)
  }
}
