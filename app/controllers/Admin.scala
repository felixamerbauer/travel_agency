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
import models.Airline
import views.html._
import views.formdata.AdminAirlineFormData

object Admin extends Controller {
  val data = Seq(Airline(1, "a", "A"), Airline(2, "b", "B"))

  private val airlineForm = Form(
    mapping(
      "id" -> text,
      "name" -> text,
      "apiURL" -> text)(AdminAirlineFormData.apply)(AdminAirlineFormData.unapply))

  def airlines = Action { implicit request =>
    Ok(admin.airlines(loginForm, authenticated, data))
  }

  def airline(id: Option[Int]) = Action { implicit request =>
    val form = id match {
      case Some(id) => airlineForm.fill(new AdminAirlineFormData(data.find(_.id == id).get))
      case None => airlineForm
    }
    Ok(admin.airline(loginForm, authenticated, form))
  }

  def airlinePost() = Action { implicit request =>
    val filledForm: Form[AdminAirlineFormData] = airlineForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form hat errors ${formWithErrors.data}")
        Redirect(routes.Admin.airlines)
      },
      formData => {
        info(s"form is ok $formData")
        Redirect(routes.Admin.airlines)
      })
    Redirect(routes.Admin.airlines)
  }

}