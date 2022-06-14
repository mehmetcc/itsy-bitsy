package spider

import config.{AppConfig, ConfigurationService}
import http.URL
import org.jsoup.nodes.Document
import zio.{IO, Task, ZIO, ZLayer}

trait SpiderRunner {
  def run[E](router: URL => Set[URL], processor: (URL, Document) => IO[E, Unit]): Task[List[E]]
}

object SpiderRunner {
  def run[E](
    router: URL => Set[URL],
    processor: (URL, Document) => IO[E, Unit]
  ): ZIO[SpiderRunner, Throwable, List[E]] =
    ZIO.serviceWithZIO[SpiderRunner](_.run(router, processor))
}

case class SpiderRunnerLive(spider: Spider, configuration: AppConfig) extends SpiderRunner {
  override def run[E](
    router: URL => Set[URL],
    processor: (URL, Document) => IO[E, Unit]
  ): Task[List[E]] = for {
    - <-
      ZIO.log(
        s"Starting spider with ${configuration.runConfig.seeds.size} seeds on ${configuration.runConfig.fiberCount} fibers"
      )
    errors <- spider.visit(configuration.runConfig.seeds.toSet, router, processor)(
                configuration.runConfig.fiberCount
              )
  } yield errors
}

object SpiderRunnerLive {
  val layer: ZLayer[Spider with ConfigurationService, Throwable, SpiderRunnerLive] = ZLayer {
    for {
      configuration <- ConfigurationService.load
      spider        <- ZIO.service[Spider]
    } yield SpiderRunnerLive(spider, configuration)
  }
}
