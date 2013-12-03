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
import controllers.Security._

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index(loginForm, authenticated))
  }

  private val searchForm = Form(
    mapping(
      "from" -> list(text),
      "to" -> list(text),
      "start" -> text,
      "end" -> text,
      "adults" -> list(text),
      "children" -> list(text),
      "category" -> list(text))(SearchFormData.apply)(SearchFormData.unapply))

  private val persons = Map("1" -> false, "2" -> true, "3" -> false, "4" -> false, "5" -> false)
  private val from = Map("Mailand" -> true, "Madrid" -> false)
  private val to = Map("New York" -> true, "Johannesburg" -> false)
  private val category = Map("1+" -> false, "2+" -> false, "3+" -> true, "4+" -> false, "5+" -> false)

  def travelSearch = Action { implicit request =>
    val data = SearchFormData()
    Ok(views.html.search(loginForm, searchForm, from, to, persons, category, authenticated))
  }

  def travelSearchPost = Action { implicit request =>
    val data = SearchFormData()
    Ok(views.html.search(loginForm, searchForm, from, to, persons, category, authenticated))
  }

  def travelList = Action { implicit request =>
    Ok(views.html.list(loginForm, authenticated))
  }

  def travelBooking = Action { implicit request =>
    Ok(views.html.booking(loginForm, authenticated))
  }

  def travelBookingConfirmation = Action { implicit request =>
    Ok(views.html.bookingconfirmation(loginForm, authenticated))
  }

  def http404(any: String) = Action { NotFound }

}