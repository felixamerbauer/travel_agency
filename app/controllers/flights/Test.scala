package controllers.flights
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick._
import play.api.Play.current
import play.api.libs.json.Reads

object Test extends Controller {
  def list(page: Int, orderBy: Int, filter: String) = DBAction { implicit rs =>
  	rs.request.body.asJson
    Ok("")
  }
}