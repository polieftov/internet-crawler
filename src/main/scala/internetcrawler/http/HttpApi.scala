package internetcrawler.http

import cats.effect.{Concurrent, Resource, Sync}
import cats.implicits.*
import internetcrawler.core.Crawler
import org.http4s.*
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

class HttpApi[F[_] : Concurrent : Logger] private(crawler: Crawler[F]) {
  val endpoints: HttpRoutes[F] = Router("/crawler/api" -> Routes[F](crawler).routes)
}

object HttpApi {
  def apply[F[_] : Concurrent : Logger](crawler: Crawler[F]): Resource[F, HttpApi[F]] =
    new HttpApi[F](crawler).pure
}
