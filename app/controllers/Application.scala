package controllers

import scala.collection.immutable.TreeMap
import controllers.Security.authenticated
import controllers.Security.loginForm
import models.Customer
import models.TCustomer
import models.TUser
import models.User
import play.api.Logger.info
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.db.slick.Config.driver.simple.columnBaseToInsertInvoker
import play.api.db.slick.DBAction
import play.api.i18n.Lang
import play.api.i18n.Messages
import play.api.mvc.Action
import play.api.mvc.Controller
import views.formdata.Commons.creditCardCompaniesFirstSelected
import views.formdata.Commons.dateFormat
import views.formdata.Commons.sexesFirstSelected
import views.formdata.Commons.sexesFormStringType
import views.formdata.RegistrationFormData
import views.formdata.SearchFormData
import sun.org.mozilla.javascript.internal.SecurityController

object Application extends Controller {

  def index = Action { implicit request =>
    request.acceptLanguages
    println("Locale " + request.acceptLanguages)
    println("msg " + Messages("index.header"))
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

  def search = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    val directions = Client.fetchDirections
    from = TreeMap(directions.map(_.from -> false).toSeq: _*)
    to = TreeMap(directions.map(_.to -> false).toSeq: _*)
    from = from + (from.head._1 -> true)
    to = to + (to.head._1 -> true)
    Ok(views.html.search(loginForm, defaultSearchForm, from, to, persons, category, authenticated))
  }

  def searchPost = DBAction { implicit rs =>
    val filledForm: Form[SearchFormData] = searchForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form has errors ${formWithErrors.data}")
        BadRequest(views.html.search(loginForm, formWithErrors, from, to, persons, category, authenticated))
      },
      formData => {
        info(s"form is ok $formData")
        Redirect(routes.Application.list(formData.from, formData.to, formData.start, formData.end, formData.adults.toInt, formData.children.toInt))
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

  def list(from: String, location: String, start: String, end: String, adults: Int, children: Int) = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    val customer = Security.curCustomer
    info(s"list $customer")
    val startDate = dateFormat.parseDateTime(start).toDateMidnight()
    val endDate = dateFormat.parseDateTime(end).toDateMidnight()
    val journeys = Client.checkAvailability(from, location, startDate, endDate, adults, children)
    Ok(views.html.list(loginForm, journeys, authenticated))
  }

  def booking(from: String, to: String, startDate: String, endDate: String, flightOutwardUrl: String, flightOutwardAirline: String, flightOutwardId: Int, flightInwardUrl: String, flightInwardAirline: String, flightInwardId: Int, hotelUrl: String, hotelName: String, hotelId: Int, adults: Int, children: Int, price: Int) = DBAction { implicit rs =>
    info(s"booking $flightOutwardUrl / $flightOutwardAirline / $flightOutwardId / $flightInwardUrl / $flightInwardAirline / $flightInwardId / $hotelUrl / $hotelName / $hotelId / $adults / $children / $price")
    implicit val dbSession = rs.dbSession
    val startDateDM = dateFormat.parseDateTime(startDate).toDateMidnight()
    val endDateDM = dateFormat.parseDateTime(endDate).toDateMidnight()
    val customer = Security.curCustomer
    Client.book(from, to, startDateDM, endDateDM, flightOutwardUrl, flightOutwardAirline, flightOutwardId, flightInwardUrl, flightInwardAirline, flightInwardId, hotelUrl, hotelName, hotelId, adults, children, price, customer)
    Ok(views.html.booking(loginForm, authenticated))
  }

  def bookingConfirmation = Action { implicit request =>
    Ok(views.html.bookingconfirmation(loginForm, authenticated))
  }

  def http404(any: String) = Action { NotFound }

  case class MyLocale(locale: String)
  val localeForm = Form(
    mapping("locale" -> nonEmptyText)(MyLocale.apply)(MyLocale.unapply))

  val changeLocale = Action { implicit request =>
    val referrer = request.headers.get(REFERER).getOrElse("/")
    val filledForm = localeForm.bindFromRequest
    filledForm.fold(
      errors => {
        info("The locale can not be change to : " + errors.get)
        BadRequest(referrer)
      },
      locale => {
        info("Change user lang to : " + locale)
        Redirect(referrer).withLang(Lang(locale.locale)) // TODO Check if the lang is handled by the application
      })
  }

}