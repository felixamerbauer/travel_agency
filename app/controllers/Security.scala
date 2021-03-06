package controllers

import scala.slick.session.Session
import db.QueryMethods.checkLogin
import db.QueryMethods.userCustomer
import models.Customer
import play.api.Logger._
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.db.slick.DBAction
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import views.formdata.LoginFormData
import db.QueryMethods
import models.User
import play.api.db.slick.DBSessionRequest
import play.api.mvc.SimpleResult

object Security extends Controller {

  val loginForm = Form(
    mapping(
      "email" -> text,
      "password" -> text)(LoginFormData.apply)(LoginFormData.unapply))

  def authenticated(implicit request: Request[AnyContent]) = (request.session.get("user"), request.session.get("name")) match {
    case (Some(user), Some(name)) => Some(user, name)
    case _ => None
  }

  def checkAuthenticated(code: => SimpleResult)(implicit request: DBSessionRequest[AnyContent]): SimpleResult = {
    if (authenticated.isDefined) code
    else
      Redirect(routes.Application.index())
  }

  def userCustomer(implicit request: Request[AnyContent], session: Session): Option[Tuple2[User, Customer]] = {
    info(s"userCustomer $authenticated")
    authenticated map (e => QueryMethods.userCustomer(e._1))
  }

  def login = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    def refer(user: Option[Tuple2[User, Customer]]) = {
      rs.request.headers.get("Referer") match {
        case Some(referer) =>
          info("Redirect to referer " + referer)
          user match {
            // todo set login failed
            case Some(userCustomer) =>
              val (user, customer) = userCustomer
              Redirect(referer).withSession("user" -> user.email, "name" -> customer.fullName)
            case None =>
              info("Redirecting login failed")
              Redirect(referer).flashing("loginFailed" -> "true")
          }
        case None =>
          warn("No referer found -> redirect to start page")
          Redirect(routes.Application.index())
      }
    }

    val filledForm: Form[LoginFormData] = loginForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form hat errors ${formWithErrors.data}")
        Redirect(routes.Application.index())
      },
      loginData => {
        info(s"form is ok '${loginData.email}'/'${loginData.password}'")
        // special case admin
        if (loginData.email == "top" && loginData.password == "secret") {
          info("Storing user login in session")
          Redirect(routes.Application.index()).withSession(
            "user" -> "admin",
            "name" -> "Admin")
        } else {
          checkLogin(loginData.email, loginData.password) match {
            case Some(userCustomer) =>
              val (user, _) = userCustomer
              info(s"user ${user.email} authenticated")
              refer(Some(userCustomer))
            case None =>
              info("wrong password")
              refer(None)
          }
        }
      })
  }
  def logout = Action { implicit request =>
    info("logout")
    Redirect(routes.Application.index()).withNewSession
  }

}