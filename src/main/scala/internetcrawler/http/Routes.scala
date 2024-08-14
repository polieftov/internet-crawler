package internetcrawler.http

import cats.effect.Concurrent
import cats.implicits.*
import internetcrawler.core.Crawler
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger

class Routes[F[_] : Concurrent] private(crawler: Crawler[F])(implicit logger: Logger[F]) extends Http4sDsl[F] {

  val routes = Router(
    "search" -> HttpRoutes.of[F] {
      case req@POST -> Root =>
        for {
          urls <- req.as[Seq[String]].onError {
            case er =>
              logger.error(er)("Failed to parse requests body")
          }
          crawlerR <- crawler.getTagContent(urls)
          res = crawlerR.collect {
            case Right((uri, content)) => (uri.toString, content)
          }
          resp <- Ok(res)
        } yield resp
    }
  )
}

object Routes {
  def apply[F[_] : Concurrent : Logger](crawler: Crawler[F]): Routes[F] = new Routes[F](crawler)
}