package internetcrawler.http

import cats.effect.{Async, Resource}
import cats.implicits.*
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.typelevel.log4cats.Logger

trait CrawlerClient[F[_]] {
  def sendReq(uri: Uri): F[String]
}

class LiveCrawlerClient[F[_] : Async] private(implicit logger: Logger[F]) extends CrawlerClient[F] {
  private val clientResource: Resource[F, Client[F]] = EmberClientBuilder.default[F].build

  override def sendReq(uri: Uri): F[String] = clientResource.use { client =>
    client.expect[String](uri).onError {
      case exc: Throwable => logger.warn(s"Exception while sending request to $uri")
    }
  }
}

object LiveCrawlerClient {
  def apply[F[_] : Async : Logger](): Resource[F, LiveCrawlerClient[F]] = new LiveCrawlerClient[F]().pure
}