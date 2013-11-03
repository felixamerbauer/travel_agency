package controllers.airline

import play.api.mvc.Controller
import play.api.db.slick.DBAction
import play.api.Play.current
import controllers.CtrlHelper
import models.flights._
import models.flights.Airports.Airports
import models.flights.Continent._
import play.api.libs.json.Json.toJson
import controllers.JsonDeSerialization._
import play.api.Logger._
import models.flights.Airport

object FlightsCtrl extends Controller with CtrlHelper {
  case class Direction(from: Airport, to: Airport)
  private val data: Seq[Flight] = {
    val src = Airports.filter(_.continent == Europe)
    val dst = Airports.filter(_.continent != Europe)
    val directions = (for {
      from <- src
      to <- dst
    } yield Seq(Direction(from, to), Direction(to, from))).flatten
    for ((a, b) <- directions.zipWithIndex)
      yield (Flight(b, a.from.iata, a.to.iata))
  }

  def list(airline: String, from: Option[String], to: Option[String], start: Option[String], end: Option[String]) = DBAction { implicit rs =>
    info(s"list airline:$airline, from=$from to=$to start=$start end=$end")

    val fromTo = (from, to) match {
      case (Airport(from), Airport(to)) =>
        info("check 1")
        Direction(from, to)
      case _ => None
    }
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