package controllers.ext

import scala.Option.option2Iterable

import controllers.CtrlHelper
import controllers.JsonDeSerialization.directionWrites
import controllers.JsonDeSerialization.flightBookingRequestReads
import controllers.JsonDeSerialization.flightBookingResponseWrites
import controllers.JsonDeSerialization.flightWrites
import db.QueryBasics.dateTimeMapper
import db.QueryLibrary.qFlight
import db.QueryLibrary.qFlightBookingWithFlight
import db.QueryLibrary.qFlightsWithLocation
import db.QueryMethods.bookFlightSeats
import db.QueryMethods.cancelFlightSeats
import json.Direction
import json.FlightBookingRequest
import json.FlightBookingResponse
import json.FlightJson
import models.TLocation
import models.ext.TExtFlight
import play.api.Logger.error
import play.api.Logger.info
import play.api.Logger.warn
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.Config.driver.simple.columnExtensionMethods
import play.api.db.slick.Config.driver.simple.queryToQueryInvoker
import play.api.db.slick.Config.driver.simple.valueToConstColumn
import play.api.db.slick.DBAction
import play.api.libs.json.Json
import play.api.libs.json.Json.toJson
import play.api.mvc.Controller

object FlightsCtrl extends Controller with CtrlHelper {

  def directions(airline: String) = DBAction { implicit rs =>
    info(s"directions $airline")
    implicit val dbSession = rs.dbSession
    val query = for {
      (_, from, to) <- qFlightsWithLocation(airline)
    } yield (from.iataCode, to.iataCode)
    val data = query.to[Set].map(e => Direction(e._1, e._2))
    Ok(toJson(data))
  }

  // e.g. http://127.0.0.1:9000/airline/austrian/flights?from=JFK&to=BER&start=2013-11-16T15:00:00%2B01:00&end=2014-11-16T15:00:00%2B01:00
  def list(airline: String, from: Option[String], to: Option[String], start: Option[String], end: Option[String]) = DBAction { implicit rs =>
    info(s"list airline=$airline, from=$from to=$to start=$start end=$end")
    implicit val dbSession = rs.dbSession
    // check if start and end are valid dates
    val startDT = start flatMap parseDateTime
    val endDT = end flatMap parseDateTime

    // build the dynamic query parts
    def flightConditions(flight: TExtFlight.type, toLocation: TLocation.type, fromLocation: TLocation.type): Column[Boolean] = {
      val conditions: List[Column[Boolean]] = List(
        Some(flight.apiUrl === airline),
        startDT map (flight.dateTime >= _),
        endDT map (flight.dateTime <= _),
        to map (toLocation.iataCode === _),
        from map (fromLocation.iataCode === _)).flatten
      conditions.reduce(_ && _)
    }

    // build the complete query including dynamic parts
    val query = for { (flight, to, from) <- qFlightsWithLocation if (flightConditions(flight, to, from)) } yield (flight, from, to)
    //    info("query\n" + query.selectStatement)

    // perform the query
    val data = query.to[Vector]

    // build the json from the returned data
    //    info("data\n\t" + data.mkString("\n\t"))
    val json = for ((flight, from, to) <- data) yield {
      new FlightJson(flight, from, to)
    }
    Ok(toJson(json))
  }

  def find(apiUrl: String, id: Int) = DBAction { implicit rs =>
    info(s"find apiUrl=$apiUrl id=$id")
    implicit val dbSession = rs.dbSession
    qFlightsWithLocation(apiUrl, id).to[Vector] match {
      // good case: single result
      case Vector(flightWithLocations) =>
        info(s"Flight for apiUrl $apiUrl adn id $id found $flightWithLocations -> returning 200 (with json body)")
        val flightJson = new FlightJson(flight = flightWithLocations._1, from = flightWithLocations._2, to = flightWithLocations._3)
        Ok(toJson(flightJson))
      // bad case 1: no result
      case Vector() =>
        warn(s"No flight for apiUrl $apiUrl and id $id -> returning 404")
        NotFound
      // error case 1: unexpected result
      case e =>
        error(s"unknown data for apiUrl $apiUrl and id $id $e returning 500")
        InternalServerError
    }
  }

  def book(apiUrl: String, id: Int) = DBAction { implicit rs =>
    val bookingDetails = parse[FlightBookingRequest]
    info(s"book airline=$apiUrl id=$id bookingDetails=$bookingDetails")
    implicit val dbSession = rs.dbSession
    qFlight(apiUrl, id).to[Vector] match {
      // good case: single result
      case Vector(flight) =>
        info(s"Flight for apiUrl $apiUrl adn id $id found $flight")
        if (flight.availableSeats >= bookingDetails.seats) {
          bookFlightSeats(flight.id, bookingDetails.seats) match {
            case Some(bookingId) =>
              info(s"booking went well -> returning 201 and booking id $bookingId in json response")
              val response = new FlightBookingResponse(apiUrl, bookingId)
              Created(Json.toJson(response))
            case None =>
              warn("booking failed (was attempted) -> returning 409")
              // TODO add explanation in body
              Conflict
          }
        } else {
          warn("booking failed (wasn't attempted because not enough seats) -> returning 409")
          // TODO add explanation in body
          Conflict
        }
      // bad case 1: no result
      case Vector() =>
        warn(s"No flight for apiUrl $apiUrl and id $id -> returning 404")
        NotFound
      // error case 1: unexpected result
      case e =>
        error(s"unknown data for apiUrl $apiUrl and id $id $e -> returning 500")
        InternalServerError
    }
  }

  def cancel(apiUrl: String, bookingId: Int) = DBAction { implicit rs =>
    info(s"cancel apiUrl=$apiUrl bookingId=$bookingId")
    implicit val dbSession = rs.dbSession
    qFlightBookingWithFlight(apiUrl, bookingId).to[Vector] match {
      // good case: single result
      case Vector(bookingFlight) =>
        val (booking, flight) = bookingFlight
        info(s"Booking and flight for apiUrl $apiUrl and bookingId $bookingId found $booking $flight")
        if (cancelFlightSeats(flight, booking)) {
          info("cancelling went well -> returning 201")
          Created
        } else {
          warn("cancelling failed (was attempted) -> returning 409")
          // TODO add explanation in body
          Conflict
        }
      // bad case 1: no result
      case Vector() =>
        warn(s"No booking and flight for apiUrl $apiUrl and bookingId $bookingId -> returnig 404")
        NotFound
      // error case 1: unexpected result
      case e =>
        error(s"unknown data for apiUrl $apiUrl and bookingId $bookingId $e -> returnig 500")
        InternalServerError
    }
  }

}