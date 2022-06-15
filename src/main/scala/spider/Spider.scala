package spider

import http.{Page, URL}
import org.jsoup.nodes.Document
import zio.{IO, Ref, Task, UIO, ULayer, ZIO, ZLayer}

trait Spider {
  def visit[E](seeds: Set[URL], router: URL => Set[URL], processor: (URL, Document) => IO[E, Unit])(
    maxNumberOfFibers: Int
  ): Task[List[E]]
}

object Spider {
  def visit[E](
    seeds: Set[URL],
    router: URL => Set[URL],
    processor: (URL, Document) => IO[E, Unit]
  )(maxNumberOfFibers: Int): ZIO[Spider, Throwable, List[E]] =
    ZIO.serviceWithZIO[Spider](_.visit(seeds, router, processor)(maxNumberOfFibers))
}

case class SpiderLive() extends Spider {
  override def visit[E](
    seeds: Set[URL],
    router: URL => Set[URL],
    processor: (URL, Document) => IO[E, Unit]
  )(maxNumberOfFibers: Int): Task[List[E]] = for {
    ref     <- Ref.make(SpiderState(seeds, List.empty[E]))
    compute <- ZIO.attemptBlocking(visitParallelN(seeds, router, processor)(ref, maxNumberOfFibers)).flatten
    state   <- ref.get
  } yield state.errors

  private def visitParallelN[E](seeds: Set[URL], router: URL => Set[URL], processor: (URL, Document) => IO[E, Unit])(
    ref: Ref[SpiderState[E]],
    n: Int
  ): Task[List[E]] = ZIO
    .foreachPar(seeds)(seed => visitOnce(seed, router, processor)(ref))
    .withParallelism(n)
    .map(result => result.flatten)
    .flatMap(newURLs => visitParallelN(newURLs, router, processor)(ref, n))

  private def visitOnce[E](seed: URL, router: URL => Set[URL], processor: (URL, Document) => IO[E, Unit])(
    ref: Ref[SpiderState[E]]
  ): Task[Set[URL]] =
    for {
      _       <- ZIO.log("Processing: " + seed.url)
      page    <- ZIO.fromOption(Page.make(seed)).orElse(ZIO.succeed(Page.empty))
      scraped  = page.extractURLs.flatMap(router)
      either  <- processor(seed, page.document).either
      newURLs <- getDifference(ref, scraped, either)
    } yield newURLs

  private def getDifference[E](ref: Ref[SpiderState[E]], scraped: Set[URL], processed: Either[E, Unit]): UIO[Set[URL]] =
    ref.modify(state =>
      (
        scraped -- state.visited, {
          val s2 = state.visitAll(scraped); processed.fold(s2.logError, _ => s2)
        }
      )
    )
}

object SpiderLive {
  def layer: ULayer[SpiderLive] = ZLayer.succeed(SpiderLive())
}
