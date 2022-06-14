import config.ConfigurationServiceLive
import http.URL
import org.jsoup.nodes.Document
import spider.{SpiderLive, SpiderRunner, SpiderRunnerLive}
import zio._

object Main extends zio.ZIOAppDefault {
  private val Processor: (URL, Document) => Task[Unit] = (url, doc) =>
    for {
      result <- ZIO.succeed(List(url -> doc))
    } yield result

  private val Router: URL => Set[URL] = url => if (url.parsed.apexDomain.contains("google.com")) Set(url) else Set()

  private final val program = for {
    _ <- ZIO.log("Starting Spider...")
    _ <- SpiderRunner.run(Router, Processor)
  } yield ()

  override def run: Task[Unit] =
    program.provide(ConfigurationServiceLive.layer, SpiderRunnerLive.layer, SpiderLive.layer)
}
