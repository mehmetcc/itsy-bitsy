package config

import http.URL
import zio.Scope
import zio.test._

object ConfigurationServiceSpec extends ZIOSpecDefault {
  val seeds = List(
    URL.make("https://www.google.com").get,
    URL.make("https://www.facebook.com").get,
    URL.make("https://www.microsoft.com").get
  )

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("ConfigurationServiceSpec")(
    test("Should be able to load from file") {
      val program =
        for {
          config <- ConfigurationService.load
        } yield {
          assertTrue(config.runConfig.seeds == seeds) &&
          assertTrue(config.runConfig.fiberCount == 100)
        }
      program.provide(ConfigurationServiceLive.layer)
    }
  )

}
