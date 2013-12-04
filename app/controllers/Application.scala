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
import play.api.db.slick.DBAction
import scala.collection.immutable.TreeMap
import views.formdata.Commons._

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index(loginForm, authenticated))
  }

  private val searchForm = Form(
    mapping(
      "from" -> nonEmptyText,
      "to" -> nonEmptyText,
      "start" -> nonEmptyText,
      "end" -> nonEmptyText,
      "adults" -> nonEmptyText,
      "children" -> nonEmptyText,
      "category" -> nonEmptyText)(SearchFormData.apply)(SearchFormData.unapply).
      verifying(SearchFormData.constraints))
  val defaultSearchForm = searchForm.fill(SearchFormData())

  private val persons = TreeMap("0" -> false, "1" -> false, "2" -> true, "3" -> false, "4" -> false, "5" -> false)
  private val category = TreeMap("1+" -> false, "2+" -> false, "3+" -> true, "4+" -> false, "5+" -> false)
  private var from: Map[String, Boolean] = _
  private var to: Map[String, Boolean] = _

  def travelSearch = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    val directions = Client.fetchDirections
    from = TreeMap(directions.map(_.from -> false).toSeq: _*)
    to = TreeMap(directions.map(_.to -> false).toSeq: _*)
    from = from + (from.head._1 -> true)
    to = to + (to.head._1 -> true)
    Ok(views.html.search(loginForm, defaultSearchForm, from, to, persons, category, authenticated))
  }

  def travelSearchPost = DBAction { implicit rs =>
    val filledForm: Form[SearchFormData] = searchForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form has errors ${formWithErrors.data}")
        BadRequest(views.html.search(loginForm, formWithErrors, from, to, persons, category, authenticated))
      },
      formData => {
        info(s"form is ok $formData")
        implicit val dbSession = rs.dbSession
        val checkFrom = formData.from
        val checkLocation = formData.to
        val checkStart = dateFormat.parseDateTime(formData.start).toDateMidnight()
        val checkEnd = dateFormat.parseDateTime(formData.end).toDateMidnight()
        // TODO store search details
        val checkPersons = formData.adults.toInt + formData.children.toInt
        val checkRooms = Math.ceil(checkPersons / 2.0).toInt
        val journeys = Client.checkAvailability(checkFrom, checkLocation, checkStart, checkEnd, checkPersons, checkRooms)
        if (journeys.isEmpty) {
          info("no journeys found")
          val okFormNoFlights = searchForm.fill(formData).withError("", "", "Keine Reisen zu ihren Suchkriterien gefunden")
          Ok(views.html.search(loginForm, okFormNoFlights, from, to, persons, category, authenticated))
        } else {
          Ok
        }
      })
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