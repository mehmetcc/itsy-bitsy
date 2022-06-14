package http

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.jdk.CollectionConverters._

case class Page private (document: Document) {
  def extractURLs: Set[URL] = document
    .select("a[href]")
    .iterator()
    .asScala
    .map(e => URL.make(e.attr("href")))
    .toSet
    .flatten
}

object Page {
  def make(url: URL): Option[Page] =
    scala.util
      .Try(
        Jsoup
          .connect(url.url)
          .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
          .referrer("http://www.google.com")
          .get()
      )
      .toOption
      .map(Page(_))

  def empty: Page = Page(
    Jsoup.parse("<html><head><title>First parse</title></head><body></body></html>")
  )
}
