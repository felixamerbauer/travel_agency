package controllers

import scala.collection.immutable.TreeMap
import scala.collection.mutable.{ LinkedHashMap, SynchronizedMap }
import controllers.Security.authenticated
import controllers.Security.loginForm
import models.Customer
import models.TCustomer
import models.TUser
import models.User
import play.api.Logger.info
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
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
import scala.collection.mutable.SynchronizedMap
import scala.collection.mutable.LinkedHashMap
import views.html.loginRegistration
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.db.slick.Config.driver.simple._
import views.formdata.LoginFormData
import db.QueryMethods.checkLogin
import play.api.db.slick.DBSessionRequest
import db.QueryMethods

object Application extends Controller {

  private object BookingCache {
    private val cache = new LinkedHashMap[Int, Journey]() with SynchronizedMap[Int, Journey]
    private val max = 100

    def put(journeys: Seq[Journey]) {
      info(s"putting ${journeys.size} in booking cache")
      cache ++= journeys map (e => e.hashCode -> e)
      val size = cache.size
      if (size > max) {
        val dropping = size - max
        info(s"Dropping first $dropping of booking cache")
        cache --= cache.keys.take(dropping)
      }
    }
    def has(journeyHash: Int): Option[Journey] = cache.get(journeyHash)
  }

  def index = Action { implicit request =>
    request.acceptLanguages
    val loginFailed = flash.get("loginFailed").isDefined
    println("Login failed = " + loginFailed)
    println("Locale " + request.acceptLanguages)
    println("msg " + Messages("index.header"))
    Ok(views.html.index(loginForm, authenticated, loginFailed))
  }

  private val searchForm = Form(
    mapping(
      "from" -> nonEmptyText,
      "to" -> nonEmptyText,
      "start" -> nonEmptyText,
      "end" -> nonEmptyText,
      "adults" -> number(min = 1, max = 5),
      "children" -> number(min = 0, max = 5),
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
    val loginFailed = flash.get("loginFailed").isDefined
    println("Login failed = " + loginFailed)
    val directions = Client.fetchDirections
    from = TreeMap(directions.map(_.from -> false).toSeq: _*)
    to = TreeMap(directions.map(_.to -> false).toSeq: _*)
    from = from + (from.head._1 -> true)
    to = to + (to.head._1 -> true)
    Ok(views.html.search(loginForm, defaultSearchForm, from, to, persons, category, authenticated, loginFailed))
  }

  def searchPost = DBAction { implicit rs =>
    val filledForm: Form[SearchFormData] = searchForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form has errors ${formWithErrors.data}")
        BadRequest(views.html.search(loginForm, formWithErrors, from, to, persons, category, authenticated, false))
      },
      formData => {
        info(s"form is ok $formData")
        // "3+" -> 3 
        val category = formData.category.take(1).toInt
        info("category " + formData.category + "/" + category)

        Redirect(routes.Application.list(formData.from, formData.to, formData.start, formData.end, formData.adults, formData.children, category))
      })
  }

  def registration = DBAction { implicit rs =>
    Ok(views.html.registration(authenticated, registrationForm, sexesFirstSelected, creditCardCompaniesFirstSelected))
  }

  def loginRegistration(journeyHash: Int) = DBAction { implicit rs =>
    val journey = BookingCache.has(journeyHash).get
    Ok(views.html.loginRegistration(authenticated, loginForm, registrationForm, sexesFirstSelected, creditCardCompaniesFirstSelected, journey, false))
  }

  def loginBooking(journeyHash: Int) = DBAction { implicit rs =>
    val journey = BookingCache.has(journeyHash).get
    val filledForm: Form[LoginFormData] = loginForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form hat errors ${formWithErrors.data}")
        BadRequest(views.html.loginRegistration(authenticated, formWithErrors, registrationForm, sexesFirstSelected, creditCardCompaniesFirstSelected, journey, false))
      },
      formData => {
        info(s"form is ok $formData")
        implicit val dbSession = rs.dbSession
        checkLogin(formData.email, formData.password) match {
          case Some(user) =>
            info(s"user ${user.email} authenticated")
            info("Redirecting to booking with just logged in user")
            val (_, customer) = QueryMethods.userCustomer(user.email)
            doBooking(journey, customer, Some(user))
          case None =>
            info("wrong password")
            BadRequest(views.html.loginRegistration(authenticated, loginForm, registrationForm, sexesFirstSelected, creditCardCompaniesFirstSelected, journey, loginFailed = true))
        }
      })
  }

  def registrationBooking(journeyHash: Int) = DBAction { implicit rs =>
    val journey = BookingCache.has(journeyHash).get
    val filledForm: Form[RegistrationFormData] = registrationForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form hat errors ${formWithErrors.data}")
        BadRequest(views.html.loginRegistration(authenticated, loginForm, formWithErrors, sexesFirstSelected, creditCardCompaniesFirstSelected, journey, false))
      },
      formData => {
        info(s"form is ok $formData")
        implicit val dbSession = rs.dbSession
        val (user, customer) = registration(formData)
        info("booking with newly registered user")
        doBooking(journey, customer, Some(user))
      })
  }

  private def registration(formData: RegistrationFormData)(implicit dbSession: Session): Tuple2[User, Customer] = {
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
      val customerId = TCustomer.forInsert.insert(customer.copy(userId = userId))
      (user.copy(id = userId), customer.copy(customerId))
    }
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
        registration(formData)
      })
    Redirect(routes.Application.index())
  }

  def list(from: String, location: String, start: String, end: String, adults: Int, children: Int, category: Int) = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    val loginFailed = flash.get("loginFailed").isDefined
    println("Login failed = " + loginFailed)
    val startDate = dateFormat.parseDateTime(start).toDateMidnight()
    val endDate = dateFormat.parseDateTime(end).toDateMidnight()
    val journeys = Client.checkAvailability(from, location, startDate, endDate, adults, children, category)
    BookingCache.put(journeys)
    // group journey by hotel
    Ok(views.html.list(loginForm, journeys.groupBy(_.hotel), authenticated, loginFailed))
  }

  private def doBooking(journey: Journey, customer: Customer, newlyLoggedInUser: Option[User] = None)(implicit session: DBSessionRequest[AnyContent]) = {
    implicit val dbSession = session.dbSession
    val result = Client.book(journey, customer) match {
      // booking successful
      case Some(order) =>
        Ok(views.html.booking(loginForm, authenticated, Some(order), Some(journey), customer))
      // booking failed
      case None =>
        Ok(views.html.booking(loginForm, authenticated, None, Some(journey), customer))
    }
    if (newlyLoggedInUser.isDefined) { result.withSession("user" -> newlyLoggedInUser.get.email) }
    else result
  }

  def booking(journeyHash: Int) = DBAction { implicit rs =>
    info(s"booking $journeyHash")
    implicit val dbSession = rs.dbSession
    Security.userCustomer match {
      case Some(userCustomer) =>
        val (user, customer) = userCustomer
        BookingCache.has(journeyHash) match {
          // journey in cache let's try to book
          case Some(journey) => doBooking(journey, customer)
          // no journey in cache
          case None =>
            Ok(views.html.booking(loginForm, authenticated, None, None, customer))

        }
      // not logged in
      case None => {
        val referer = rs.request.headers("Referer")
        Redirect(routes.Application.loginRegistration(journeyHash))
      }
    }
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