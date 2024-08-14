package internetcrawler.core

import cats.*
import cats.effect.*
import cats.syntax.all.*
import internetcrawler.config.CrawlerConfig
import internetcrawler.http.CrawlerClient
import org.http4s.Uri
import org.jsoup.*

import scala.util.Try

trait Crawler[F[_] : Async] {
  def getTagContent(sources: Seq[String]): F[Seq[Either[String, (Uri, String)]]]
}

class HttpCrawler[F[_] : Async] private(client: CrawlerClient[F], crawlerConfig: CrawlerConfig) extends Crawler[F] {
  override def getTagContent(sources: Seq[String]): F[Seq[Either[String, (Uri, String)]]] =
    sources
      .map { uriStr =>
        Uri.fromString(uriStr)
          .leftMap(_.message)
          .fold(
            Either.left[String, (Uri, String)](_).pure,
            uri => {
              client.sendReq(uri).map { resp =>
                Try {
                  val doc = Jsoup.parse(resp)
                  val elements = doc.getElementsByTag(crawlerConfig.searchTag)
                  if (elements.isEmpty)
                    throw new RuntimeException(s"No ${crawlerConfig.searchTag} tag in document!")
                  else
                    elements.text()
                }.fold(
                  exc => Either.left(exc.getMessage),
                  content => Either.right(uri -> content)
                )
              }.recover {
                case exc => Left(exc.getMessage)
              }
            }
          )
      }.traverse(identity)
}

object HttpCrawler {
  def apply[F[_] : Async](client: CrawlerClient[F], crawlerConfig: CrawlerConfig): Resource[F, HttpCrawler[F]] =
    new HttpCrawler[F](client, crawlerConfig).pure
}