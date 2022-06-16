package spider

import http.URL
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._

import scala.language.postfixOps

class SpiderStateTest extends AnyFlatSpec {
  val seeds = Set(
    URL.make("https://www.google.com").get,
    URL.make("https://www.facebook.com").get,
    URL.make("https://www.microsoft.com").get
  )

  "A State" should "be able to persist new entries" in {
    val s1 = SpiderState(seeds, List.empty)
    val s2 = s1.visitAll(Set(URL.make("https://google.com/search").get))
    s2.visited should contain theSameElementsAs s1.visited + URL.make("https://google.com/search").get
  }

  it should "be able to persist new errors" in {
    val s1 = SpiderState(seeds, List.empty)
    val s2 = s1.logError(new Throwable("TestException"))
    s2.errors.toSeq.map(_.getMessage) should contain theSameElementsAs (s1.errors.toSeq :+ new Throwable(
      "TestException"
    )).map(_.getMessage)
  }
}
