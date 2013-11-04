package controllers.flights

import scala.util.Try
import controllers.CtrlHelper
import controllers.JsonDeSerialization._
import controllers.JsonHelper.isoDtf
import models.flights.Airport
import models.flights.Data.Flights
import models.flights.Direction
import models.flights.Window
import models.json.flights.FlightJson
import play.api.Logger._
import play.api.Play.current
import play.api.db.slick.DBAction
import play.api.libs.json.Json.toJson
import play.api.mvc.Controller
import models.json.flights.FlightJson
import play.api.libs.json.JsValue
import play.api.mvc.Request
import play.api.libs.json.Json

object FlightsCtrl extends Controller with CtrlHelper {

  private def fromTo(from: Option[String], to: Option[String]): Option[Direction] = (from, to) match {
    case (Some(from), Some(to)) => (from, to) match {
      case (Airport(from), Airport(to)) =>
        Some(Direction(from, to))
      case _ =>
        // TODO
        ???
    }
    case _ => None
  }

  private def parseDT(dateTime: String) = try {
    Some(isoDtf.parseDateTime(dateTime))
  } catch {
    case e: IllegalArgumentException =>
      warn(s"unknown iso date $dateTime", e)
      None
  }

  private def startEnd(start: Option[String], end: Option[String]): Option[Window] = (start, end) match {
    case (Some(start), Some(end)) => (parseDT(start), parseDT(end)) match {
      case (Some(start), Some(end)) =>
        Some(Window(start, end))
      case _ =>
        // TODO
        ???
    }
    case _ => None
  }

  def list(airline: String, from: Option[String], to: Option[String], start: Option[String], end: Option[String]) = DBAction { implicit rs =>
    info(s"list airline:$airline, from=$from to=$to start=$start end=$end")
    val direction = fromTo(from, to)
    val duration = startEnd(start, end)
    var data = Flights
    direction.foreach { d =>
      println(s"filter direction $d ${data.length}")
      data = data.filter(e => e.from == d.from.iata && e.to == d.to.iata)
      println(s"filter direction $d ${data.length}")
    }
    // http://127.0.0.1:9000/airline/austrian/flights?from=SYD&to=BER&start=2013-11-16T15:00:00%2B01:00&end=2013-11-16T15:00:00%2B01:00
    duration.foreach { du =>
      println(s"filter duration $du ${data.length}")
      data = data.filterNot { da =>
        da.start.isBefore(du.start) || da.end.isAfter(du.end)
      }
      println(s"filter duration $du ${data.length}")
    }
    Ok(toJson(data.map(new FlightJson(_))))
  }

  def find(airline: String, id: Int) = DBAction { implicit rs =>
    Flights.find(_.id == id).map(new FlightJson(_)) match {
      case Some(flight) => Ok(toJson(flight))
      case None => NotFound
    }
  }

  def book(airline: String, id: Int) = DBAction { implicit rs =>
    parse[FlightJson]

    Ok("")
  }

  def save(id: Int) = DBAction { implicit rs =>
    Ok("")
  }

  def create = DBAction { implicit rs =>
    Ok("")
  }
}