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
import views.formdata.RegistrationFormData
import models.Customer
import models.User
import models.TUser
import play.api.db.slick.Config.driver.simple._
import models.TCustomer

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

  private val registrationForm = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> nonEmptyText,
      "password" -> nonEmptyText,
      "birthDate" -> nonEmptyText,
      "sex" -> nonEmptyText,
      "street" -> nonEmptyText,
      "zipCode" -> nonEmptyText,
      "city" -> nonEmptyText,
      "country" -> nonEmptyText,
      "phoneNumber" -> nonEmptyText,
      "creditCardCompany" -> nonEmptyText,
      "creditCardNumber" -> nonEmptyText,
      "creditCardExpireDate" -> nonEmptyText,
      "creditCardVerificationCode" -> nonEmptyText)(RegistrationFormData.apply)(RegistrationFormData.unapply))

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
        Redirect(routes.Application.travelList(formData.from, formData.to, formData.start, formData.end, formData.adults.toInt, formData.children.toInt))
      })
  }

  def registration = DBAction { implicit rs =>
    Ok(views.html.registration(loginForm, authenticated, registrationForm, sexesFirstSelected, creditCardCompaniesFirstSelected))
  }

  def registrationPost = DBAction { implicit rs =>
    val filledForm: Form[RegistrationFormData] = registrationForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form hat errors ${formWithErrors.data}")
      },
      formData => {
        info(s"form is ok $formData")
        implicit val dbSession = rs.dbSession
        val customer = Customer(
          id = -1,
          userId = -1,
          firstName = formData.firstName,
          lastName = formData.lastName,
          birthDate = dateFormat.parseDateTime(formData.birthDate).toDateMidnight(),
          sex = sexesFormStringType(formData.sex),
          street = formData.street,
          zipCode = formData.zipCode,
          city = formData.city,
          country = formData.country,
          phoneNumber = formData.phoneNumber,
          creditCardCompany = formData.creditCardCompany,
          creditCardNumber = formData.creditCardNumber,
          creditCardExpireDate = dateFormat.parseDateTime(formData.creditCardExpireDate).toDateMidnight(),
          creditCardVerificationCode = formData.creditCardVerificationCode)
        val user = User(
          id = -1,
          email = formData.email,
          passwordHash = formData.password)
        dbSession.withTransaction {
          val userId = TUser.autoInc.insert(user)
          info(s"userid $userId")
          TCustomer.forInsert.insert(customer.copy(userId = userId))
        }
      })
    Redirect(routes.Application.index)
  }

  def travelList(from: String, location: String, start: String, end: String, adults: Int, children: Int) = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    val startDate = dateFormat.parseDateTime(start).toDateMidnight()
    val endDate = dateFormat.parseDateTime(end).toDateMidnight()
    val journeys = Client.checkAvailability(from, location, startDate, endDate, adults, children)
    Ok(views.html.list(loginForm, journeys, authenticated))
  }

  def travelBooking = Action { implicit request =>
    Ok(views.html.booking(loginForm, authenticated))
  }

  def travelBookingConfirmation = Action { implicit request =>
    Ok(views.html.bookingconfirmation(loginForm, authenticated))
  }

  def http404(any: String) = Action { NotFound }

}