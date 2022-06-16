package http

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.OptionValues._

class PageTest extends AnyFlatSpec {
  "A Page" should "be created when a url is given" in {
    val url = URL.make("http://www.google.com").get
    val page = Page.make(url)
    assert(page.value.document.location() == url.url)
  }

  it should "be able to produce an empty page" in {
    val page = Page.empty
    assert(page.document.title() == "Empty Page")
  }

  it should "be able to extract urls from a given url" in {
    val url = URL.make("http://www.google.com").get
    val page = Page.make(url)
    assert(page.value.extractURLs.nonEmpty)
  }
}
