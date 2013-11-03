package controllers

import play.api.Play.current
import play.api.cache.Cached
import play.api.mvc.Action
import play.api.mvc.Controller

object Application extends Controller {

  def index(any: String) = Cached("homePage") {
    Action {
      Ok(views.html.index())
    }
  }

  def http404(any: String) = Action { NotFound }

}