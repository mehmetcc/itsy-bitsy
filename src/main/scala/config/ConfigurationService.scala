package config

import http.URL
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio.{Task, ULayer, ZIO, ZLayer}
import ImplicitConverters._

case class AppConfig(runConfig: RunConfig)

case class RunConfig(fiberCount: Int, seeds: List[URL])

trait ConfigurationService {
  def load: Task[AppConfig]
}

object ConfigurationService {
  def load: ZIO[ConfigurationService, Throwable, AppConfig] = ZIO.serviceWithZIO[ConfigurationService](_.load)
}

case class ConfigurationServiceLive() extends ConfigurationService {
  override def load: Task[AppConfig] = ZIO.fromEither(
    ConfigSource.default
      .load[AppConfig]
      .left
      .map(pureconfig.error.ConfigReaderException.apply)
  )
}

object ConfigurationServiceLive {
  val layer: ULayer[ConfigurationServiceLive] = ZLayer.succeed(ConfigurationServiceLive())
}
