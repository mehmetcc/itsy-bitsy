package spider

import http.URL

final case class SpiderState[+E](visited: Set[URL], errors: List[E]) {
  def visitAll(urls: Set[URL]): SpiderState[E] = copy(visited = visited ++ urls)

  def logError[E1 >: E](error: E1): SpiderState[E1] = copy(errors = error :: errors)
}
