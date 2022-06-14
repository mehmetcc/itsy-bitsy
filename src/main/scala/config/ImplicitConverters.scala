package config

import http.URL
import pureconfig.{ConfigCursor, ConfigReader}

object ImplicitConverters {
  implicit val urlReader: ConfigReader[URL] = (cur: ConfigCursor) =>
    cur.asString.map { path =>
      URL.make(path) match {
        case Some(value) => value
        case None        => throw new RuntimeException("Failure to parse Seed URLs. Please check your configuration file.")
      }
    }
}
