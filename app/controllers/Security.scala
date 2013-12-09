package controllers

import scala.slick.session.Session

import db.QueryMethods.checkLogin
import db.QueryMethods.userCustomer
import models.Customer
import play.api.Logger.info
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

object Security extends Controller {

  val loginForm = Form(
    mapping(
      "email" -> text,
      "password" -> text)(LoginFormData.apply)(LoginFormData.unapply))

  def authenticated(implicit request: Request[AnyContent]) = {
    val x = request.session.get("user")
    info(s"session.user $x")
    x
  }

  def curCustomer(implicit request: Request[AnyContent], session: Session): Customer = {
    info(s"curCustomer $authenticated")
    for ((k, v) <- request.session.data) {
      info(s"k $k v $v")
    }
    val (_, customer) = userCustomer(authenticated.get)
    customer
  }

  def login = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    val filledForm: Form[LoginFormData] = loginForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form hat errors ${formWithErrors.data}")
        Redirect(routes.Application.index)
      },
      loginData => {
        info(s"form is ok '${loginData.email}'/'${loginData.password}'")
        // special case admin
        if (loginData.email == "top" && loginData.password == "secret") {
          info("Storing user login in session")
          Redirect(routes.Application.index).withSession(
            "user" -> "admin")
        } else {
          checkLogin(loginData.email, loginData.password) match {
            case Some(user) =>
              info(s"user ${user.email} authenticated")
              Redirect(routes.Application.index).withSession(
                "user" -> user.email)
            case None =>
              info("wrong password")
              Redirect(routes.Application.index)
          }
        }
      })
  }
  def logout = Action { implicit request =>
    info("logout")
    Redirect(routes.Application.index).withNewSession
  }

}