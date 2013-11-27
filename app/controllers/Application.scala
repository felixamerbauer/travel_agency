package controllers

import play.api.Play.current
import play.api.cache.Cached
import play.api.mvc.Action
import play.api.mvc.Controller
import views.formdata.SearchFormData
import play.api.data.Form
import play.api.Logger.info
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Action
import play.api.mvc.Controller
import controllers.Security.loginForm

object Application extends Controller {

  def index(any: String) = Cached("homePage") {
    Action {
      Ok(views.html.index(loginForm))
    }
  }
  private val searchForm = Form(
    mapping(
      "from" -> list(text),
      "to" -> list(text),
      "start" -> text,
      "end" -> text,
      "travellers" -> list(text))(SearchFormData.apply)(SearchFormData.unapply))
  private val travellers = Map("1" -> false, "2" -> true, "3" -> false, "4" -> false, "5" -> false)
  private val from = Map("Mailand" -> true, "Madrid" -> false)
  private val to = Map("New York" -> true, "Johannesburg" -> false)

  def travelSearch = Action {
    val data = SearchFormData()
    Ok(views.html.travelsearch(loginForm, searchForm, from, to, travellers))
  }

  def travelSearchPost = Action {
    val data = SearchFormData()
    Ok(views.html.travelsearch(loginForm, searchForm, from, to, travellers))
  }

  def travelList = Action {
    Ok(views.html.travellist(loginForm))
  }

  def travelBooking = Action {
    Ok(views.html.travelbooking(loginForm))
  }

  def travelBookingConfirmation = Action {
    Ok(views.html.travelbookingconfirmation(loginForm))
  }

  def http404(any: String) = Action { NotFound }

}