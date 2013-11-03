import play.api.Application
import play.api.GlobalSettings
import play.api.Logger
import play.api.mvc.WithFilters
import play.filters.gzip.GzipFilter

object Util {
  val contentTypes = Set("text/html", "application/json", "application/javascript", "text/css")
  val filter = new GzipFilter(shouldGzip = (request, response) => {
    response.headers.get("Content-Type").exists(e => contentTypes.exists(f => e.startsWith(f)))
  })
}

object Global extends WithFilters(Util.filter) with GlobalSettings {
  override def onStart(application: Application) {
    println(s"logger  trace ${Logger.isTraceEnabled} debug ${Logger.isDebugEnabled} info ${Logger.isInfoEnabled} warn ${Logger.isWarnEnabled}")
    Logger.debug("onStart")
  }
  override def onStop(application: Application) {
    Logger.info("Global onStop")
  }
}