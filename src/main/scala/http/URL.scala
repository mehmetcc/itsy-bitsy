package http

/** Taken from https://www.youtube.com/watch?v=08eSR0vn4Vw */
final case class URL private (parsed: io.lemonlabs.uri.Url) {

  import io.lemonlabs.uri._

  def relative(page: String): Option[URL] =
    scala.util
      .Try(parsed.path match {
        case Path(parts) =>
          val whole = parts.dropRight(1) :+ page.dropWhile(_ == '/')

          parsed.withPath(UrlPath(whole))
      })
      .toOption
      .map(new URL(_))

  override def equals(a: Any): Boolean = a match {
    case that: URL => this.url == that.url
    case _         => false
  }

  override def hashCode: Int = url.hashCode

  def url: String = parsed.toString
}

object URL {

  import io.lemonlabs.uri._

  def make(url: String): Option[URL] =
    scala.util.Try(AbsoluteUrl.parse(url)).toOption match {
      case None         => None
      case Some(parsed) => Some(new URL(parsed))
    }
}
