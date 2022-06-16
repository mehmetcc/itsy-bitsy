package http

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.OptionValues._

class URLTest extends AnyFlatSpec {
  "A URL" should "be created from a string" in {
    val google = "http://www.google.com"
    val url = URL.make(google)
    assert(url.value.url == google)
  }

  it should "be able to complete a relative path" in {
    val url = URL.make("http://www.google.com/")
    assert(url.value.relative("/search").value.url == "http://www.google.com/search")
  }
}
