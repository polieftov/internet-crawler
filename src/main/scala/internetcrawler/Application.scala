package internetcrawler

import cats.effect.{IO, IOApp, Resource}
import internetcrawler.config.AppConfig
import internetcrawler.config.syntax.*
import internetcrawler.core.HttpCrawler
import internetcrawler.http.{HttpApi, LiveCrawlerClient}
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource

object Application extends IOApp.Simple {
  given logger: Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = ConfigSource.default.loadF[IO, AppConfig].flatMap {
    case AppConfig(crawlerConfig, emberConfig) =>
      val appResource = for {
        client <- LiveCrawlerClient[IO]()
        crawler <- HttpCrawler[IO](client, crawlerConfig)
        httpApi <- HttpApi[IO](crawler)
        server <- EmberServerBuilder
          .default[IO]
          .withHost(emberConfig.host)
          .withPort(emberConfig.port)
          .withHttpApp(httpApi.endpoints.orNotFound)
          .build
        _ <- Resource.Eval(
          IO.println(s"Server started at http://${emberConfig.host}:${emberConfig.port}/crawler/api/search")
        )
      } yield server

      appResource.use(_ => IO.println("server ready!") *> IO.never)
  }
}
