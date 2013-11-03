package controllers.airline

import play.api.mvc.Controller
import play.api.db.slick.DBAction
import play.api.Play.current
import controllers.CtrlHelper
import models.Flight
import models.Airports.Airports
import models.Continent._
import play.api.libs.json.Json.toJson
import controllers.JsonDeSerialization._

object FlightsCtrl extends Controller with CtrlHelper {
  private val data:Seq[Flight] = {
    val src = Airports.filter(_.continent == Europe)
    val dst = Airports.filter(_.continent != Europe)
    val directions = (for {
      from <- src
      to <- dst
    } yield Seq((from, to), (to, from))).flatten
    for ((a, b) <- directions.zipWithIndex)
      yield (Flight(b, a._1.iata, a._2.iata))
  }

  def list = DBAction { implicit rs =>
    Ok(toJson(data))
  }

  def find(id: Int) = DBAction { implicit rs =>
    Ok("")
  }

  def remove(id: Int) = DBAction { implicit rs =>
    Ok("")
  }

  def save(id: Int) = DBAction { implicit rs =>
    Ok("")
  }

  def create = DBAction { implicit rs =>
    Ok("")
  }
}