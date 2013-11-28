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
import views.formdata.LoginFormData
import views.html._
import play.api.mvc.Request
import play.api.mvc.AnyContent

object Security extends Controller {

  val loginForm = Form(
    mapping(
      "email" -> text,
      "password" -> text)(LoginFormData.apply)(LoginFormData.unapply))

  def authenticated(implicit request: Request[AnyContent]): Boolean = request.session.get("user").isDefined

  def login = Action { implicit request =>
    val filledForm: Form[LoginFormData] = loginForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form hat errors ${formWithErrors.data}")
        Redirect("/")
      },
      loginData => {
        info(s"form is ok '${loginData.email}'/'${loginData.password}'")
        if (loginData.email == "top" && loginData.password == "secret") {
          info("Storing user login in session")
          Redirect("/").withSession(
            "user" -> loginData.email)
        } else {
          info("wrong password")
          Redirect("/")
        }
      })
  }
  def logout = Action { implicit request =>
    info("logout")
    Redirect("/").withNewSession
  }

}