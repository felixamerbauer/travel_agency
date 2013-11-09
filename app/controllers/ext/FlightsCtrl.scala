package controllers.ext

import scala.util.Try
import controllers.CtrlHelper
import controllers.JsonDeSerialization._
import controllers.JsonHelper.isoDtf
import models.json.ext.FlightJson
import play.api.Logger._
import play.api.Play.current
import play.api.db.slick.DBAction
import play.api.libs.json.Json.toJson
import play.api.mvc.Controller
import models.json.ext.FlightJson
import play.api.libs.json.JsValue
import play.api.mvc.Request
import play.api.libs.json.Json
import db.QueryBasics.qExtFlight
import db.QueryLibrary._
import play.api.db.slick.Config.driver.simple._
import models.json.ext.FlightJson

object FlightsCtrl extends Controller with CtrlHelper {

  private def parseDT(dateTime: String) = try {
    Some(isoDtf.parseDateTime(dateTime))
  } catch {
    case e: IllegalArgumentException =>
      warn(s"unknown iso date $dateTime", e)
      None
  }

  //  private def startEnd(start: Option[String], end: Option[String]): Option[Window] = (start, end) match {
  //    case (Some(start), Some(end)) => (parseDT(start), parseDT(end)) match {
  //      case (Some(start), Some(end)) =>
  //        Some(Window(start, end))
  //      case _ =>
  //        // TODO
  //        ???
  //    }
  //    case _ => None
  //  }

  def list(airline: String, from: Option[String], to: Option[String], start: Option[String], end: Option[String]) = DBAction { implicit rs =>
    info(s"list airline:$airline, from=$from to=$to start=$start end=$end")
    //    
    //    val direction = fromTo(from, to)
    //    val duration = startEnd(start, end)
    //    var data = Flights
    //    direction.foreach { d =>
    //      println(s"filter direction $d ${data.length}")
    //      data = data.filter(e => e.from == d.from.iata && e.to == d.to.iata)
    //      println(s"filter direction $d ${data.length}")
    //    }
    //    // http://127.0.0.1:9000/airline/austrian/flights?from=SYD&to=BER&start=2013-11-16T15:00:00%2B01:00&end=2013-11-16T15:00:00%2B01:00
    //    duration.foreach { du =>
    //      println(s"filter duration $du ${data.length}")
    //      data = data.filterNot { da =>
    //        da.start.isBefore(du.start) || da.end.isAfter(du.end)
    //      }
    //      println(s"filter duration $du ${data.length}")
    //    }
    //    Ok(toJson(data.map(new FlightJson(_))))
    Ok("")
  }

  def find(airline: String, id: Int) = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    qFlightsWithLocation(airline, id).to[Vector] match {
      // good case: single result
      case Vector(flightWithLocations) =>
        info(s"Flight for airline $airline adn id $id found $flightWithLocations")
        val flightJson = new FlightJson(flight = flightWithLocations._1, from = flightWithLocations._2, to = flightWithLocations._3)
        Ok(toJson(flightJson))
      // bad case 1: no result
      case Vector() =>
        warn(s"No flight for airline $airline and id $id")
        val a: Status = NotFound
        NotFound
      // error case 1: unexpected result
      case e =>
        error(s"unknown data for airline $airline and id $id $e")
        InternalServerError
    }
  }

  def book(airline: String, id: Int) = DBAction { implicit rs =>
    parse[FlightJson]

    Ok("")
  }

  def cancel(airline: String, id: Int) = DBAction { implicit rs =>
    Ok("")
  }

}