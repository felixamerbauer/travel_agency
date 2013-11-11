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

  def travelSearch = Action {
    Ok(views.html.travelsearch())
  }

  def travelList = Action {
    Ok(views.html.travellist())
  }

  def travelBooking = Action {
    Ok(views.html.travelbooking())
  }

  def travelBookingConfirmation = Action {
    Ok(views.html.travelbookingconfirmation())
  }

  def http404(any: String) = Action { NotFound }

}