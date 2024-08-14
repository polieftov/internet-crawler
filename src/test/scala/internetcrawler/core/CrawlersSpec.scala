package internetcrawler.core

import cats.effect.*
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.implicits.*
import internetcrawler.config.CrawlerConfig
import internetcrawler.http.CrawlerClient
import org.http4s.Uri
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class CrawlersSpec extends AsyncFreeSpec
  with AsyncIOSpec
  with Matchers {

  val testClient = new CrawlerClient[IO] {
    override def sendReq(uri: Uri): IO[String] = {
      val resStr = uri.path.renderString match
        case "/empty" => ""
        case "/valid" =>
          """<!DOCTYPE HTML>
            |<html>
            | <head>
            |  <title>Test title</title>
            | </head>
            | <body>
            |  <p>...</p>
            | </body>
            |</html>
            |""".stripMargin
        case "/valid-part" =>
          """
            | <head>
            |  <title>Test part title</title>
            | </head>
            | <body>
            |  <p>...</p>
            | </body>
            |""".stripMargin
        case "/invalid" => """{ "title: 1 }"""
      resStr.pure[IO]
    }
  }
  val crawler = HttpCrawler[IO](testClient, CrawlerConfig("title"))

  "Crawler" - {
    "should return Left with exception if content is empty" in {
      val emptyUri = Uri.fromString("http://test:80/empty").toOption.get
      crawler.use(
        _.getTagContent(Seq(emptyUri.toString))
          .asserting(_ shouldBe a[Seq[Left[String, (Uri, String)]]])
      )
    }

    "should return Left with exception if content is invalid" in {
      val invalidUri = Uri.fromString("http://test:80/invalid").toOption.get
      crawler.use(
        _.getTagContent(Seq(invalidUri.toString))
          .asserting(_ shouldBe a[Seq[Left[String, (Uri, String)]]])
      )
    }

    "should return Right with tag content if content is valid html doc" in {
      val validUri = Uri.fromString("http://test:80/valid").toOption.get
      crawler.use(
        _.getTagContent(Seq(validUri.toString))
          .asserting(_ shouldBe Seq(Right[String, (Uri, String)]((validUri, "Test title"))))
      )
    }

    "should return Right with tag content if content is part of valid html doc" in {
      val validUri = Uri.fromString("http://test:80/valid-part").toOption.get
      crawler.use(
        _.getTagContent(Seq(validUri.toString))
          .asserting(_ shouldBe Seq(Right[String, (Uri, String)]((validUri, "Test part title"))))
      )
    }
  }
}
