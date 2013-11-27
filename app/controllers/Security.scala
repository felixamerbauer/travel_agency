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

object Security extends Controller {

  val loginForm = Form(
    mapping(
      "email" -> text,
      "password" -> text)(LoginFormData.apply)(LoginFormData.unapply))

  def login = Action { implicit request =>
    val filledForm: Form[LoginFormData] = loginForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info("form hat errors")
        Ok(index(loginForm))
      },
      loginData => {
        info(s"form is ok '${loginData.email}'/'${loginData.password}'")
        if (loginData.email == "top" && loginData.password == "secret") {
          info("Storing user login in session")
          Ok(index(loginForm)).withSession(
            "user" -> loginData.email)
        } else {
          info("wrong password")
          Ok(index(loginForm))
        }
      })
  }

}