package controllers.ext

import controllers.CtrlHelper
import play.api.Logger.info
import play.api.Play.current
import play.api.db.slick.DBAction
import play.api.mvc.Controller
import controllers.CtrlHelper
import controllers.JsonDeSerialization._
import controllers.JsonDeSerialization.flightWrites
import controllers.JsonHelper.isoDtf
import db.QueryBasics._
import db.QueryLibrary._
import db.QueryLibrary.qFlightsWithLocation
import db.QueryMethods._
import db.QueryMethods.cancelFlightSeats
import models.TLocation
import models.ext.TExtFlight
import json._
import play.api.Logger.error
import play.api.Logger.info
import play.api.Logger.warn
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import play.api.libs.json.Json.toJson
import play.api.mvc.Controller
import models.ext._
import play.api.libs.json.Json

object HotelsCtrl extends Controller with CtrlHelper {

  // http://127.0.0.1:9000/hotelgroup/hgX/locations
  def locations(apiUrl: String) = DBAction { implicit rs =>
    info(s"locations apiUrl=$apiUrl")
    implicit val dbSession = rs.dbSession
    val query = for {
      (_, location) <- qHotelWithLocation(apiUrl)
    } yield (location.iataCode)
    val data = query.to[Set].map(HotelLocation)
    Ok(toJson(data))
  }

  def list(apiUrl: String, location: Option[String], start: Option[String], end: Option[String]) = DBAction { implicit rs =>
    info(s"list apiUrl=$apiUrl, location=$location start=$start end=$end")
    implicit val dbSession = rs.dbSession
    // check if start and end are valid dates
    val startDate = start flatMap parseDateMidnight
    val endDate = end flatMap parseDateMidnight

    // build the dynamic query parts
    def hotelroomConditions(room: TExtHotel.type, locationDb: TLocation.type): Column[Boolean] = {
      val conditions: List[Column[Boolean]] = List(
        Some(room.apiUrl === apiUrl),
        startDate map (room.startDate >= _),
        endDate map (room.endDate <= _),
        location map (locationDb.iataCode === _)).flatten
      conditions.reduce(_ && _)
    }

    // build the complete query including dynamic parts
    val query = for { (hotelRoom, location) <- qHotelWithLocation if (hotelroomConditions(hotelRoom, location)) } yield (hotelRoom, location)
    //    info("query\n" + query.selectStatement)

    // perform the query
    val data = query.to[Vector]

    // build the json from the returned data
    //    info("data\n\t" + data.mkString("\n\t"))
    val json = for ((hotelRoom, location) <- data) yield {
      new HotelJson(hotelRoom, location)
    }
    Ok(toJson(json))
  }

  def find(apiUrl: String, id: Int) = DBAction { implicit rs =>
    info(s"find apiUrl=$apiUrl id=$id")
    implicit val dbSession = rs.dbSession
    qHotelWithLocation(apiUrl, id).to[Vector] match {
      // good case: single result
      case Vector(hotelWithLocation) =>
        info(s"Hotel for apiUrl $apiUrl and id $id found $hotelWithLocation -> returning 200 (with json body)")
        val (hotel, location) = hotelWithLocation
        val flightJson = new HotelJson(hotel, location)
        Ok(toJson(flightJson))
      // bad case 1: no result
      case Vector() =>
        warn(s"No hotel for hotelgroup $apiUrl and id $id -> returning 404")
        NotFound
      // error case 1: unexpected result
      case e =>
        error(s"unknown data for hotelgroup $apiUrl and id $id $e returning 500")
        InternalServerError
    }
  }

  def book(apiUrl: String, id: Int) = DBAction { implicit rs =>
    val bookingDetails = parse[HotelBookingRequest]
    info(s"book apiUrl=$apiUrl id=$id bookingDetails=$bookingDetails")
    implicit val dbSession = rs.dbSession
    qHotel(apiUrl, id).to[Vector] match {
      // good case: single result
      case Vector(hotel) =>
        info(s"Flight for apiUrl $apiUrl and id $id found $hotel")
        if (hotel.availableRooms >= bookingDetails.rooms) {
          bookHotelRooms(hotel.id, bookingDetails.rooms) match {
            case Some(bookingId) =>
              info(s"booking went well -> returning 201 and booking id $bookingId in json body")
              val response = new HotelBookingResponse(apiUrl, bookingId)
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
        warn(s"No hotel for apiUrl $apiUrl and id $id -> returning 404")
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
    qHotelBookingWithHotel(apiUrl, bookingId).to[Vector] match {
      // good case: single result
      case Vector(bookingHotel) =>
        val (booking, hotel) = bookingHotel
        info(s"Hotel and booking for hotelgroup $apiUrl and bookkingId $bookingId found $hotel $booking")
        if (cancelHotelRooms(hotel, booking)) {
          info("cancelling went well -> returning 201")
          Created
        } else {
          warn("cancelling failed (was attempted) -> returning 409")
          // TODO add explanation in body
          Conflict
        }
      // bad case 1: no result
      case Vector() =>
        warn(s"No hotel for hotelgroup $apiUrl and bookingId $bookingId -> returnig 404")
        NotFound
      // error case 1: unexpected result
      case e =>
        error(s"unknown data for hotelgroup $apiUrl and $bookingId $bookingId $e -> returnig 500")
        InternalServerError
    }
  }

}