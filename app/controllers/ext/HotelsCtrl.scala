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
import db.QueryMethods.bookFlightSeats
import db.QueryMethods.cancelFlightSeats
import models.TLocation
import models.ext.TExtFlight
import models.json.ext.FlightBookingDetails
import models.json.ext.FlightJson
import play.api.Logger.error
import play.api.Logger.info
import play.api.Logger.warn
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import play.api.libs.json.Json.toJson
import play.api.mvc.Controller
import models.ext.TExtHotelRoom
import models.json.ext.HotelRoomJson

object HotelsCtrl extends Controller with CtrlHelper {

  // http://127.0.0.1:9000/hotelgroup/hgX/locations
  def locations(hotelgroup: String) = DBAction { implicit rs =>
    info(s"hotelgroup=$hotelgroup")
    implicit val dbSession = rs.dbSession
    val query = for {
      (_, location) <- qHotelRoomsWithLocation(hotelgroup)
    } yield (location.iataCode)
    val data = query.to[Set]
    Ok(toJson(data))
  }

  def list(hotelgroup: String, location: Option[String], start: Option[String], end: Option[String]) = DBAction { implicit rs =>
    info(s"list hotelGroup=$hotelgroup, location=$location start=$start end=$end")
    implicit val dbSession = rs.dbSession
    // check if start and end are valid dates
    val startDate = start flatMap parseLocatDate
    val endDate = end flatMap parseLocatDate

    // build the dynamic query parts
    def hotelroomConditions(room: TExtHotelRoom.type, locationDb: TLocation.type): Column[Boolean] = {
      val conditions: List[Column[Boolean]] = List(
        Some(room.hotelShortName === hotelgroup),
        startDate map (room.startDate === _),
        endDate map (room.endDate === _),
        location map (locationDb.iataCode === _)).flatten
      conditions.reduce(_ && _)
    }

    // build the complete query including dynamic parts
    val query = for { (hotelRoom, location) <- qHotelRoomsWithLocation if (hotelroomConditions(hotelRoom, location)) } yield (hotelRoom, location)
    info("query\n" + query.selectStatement)

    // perform the query
    val data = query.to[Vector]

    // build the json from the returned data
    info("data\n\t" + data.mkString("\n\t"))
    val json = for ((hotelRoom, location) <- data) yield {
      new HotelRoomJson(hotelRoom, location)
    }
    Ok(toJson(json))
  }

  def find(hotelgroup: String, id: Int) = DBAction { implicit rs =>
    info(s"find hotelgroup=$hotelgroup id=$id")
    implicit val dbSession = rs.dbSession
    qHotelRoomsWithLocation(hotelgroup, id).to[Vector] match {
      // good case: single result
      case Vector(hotelWithLocation) =>
        info(s"Hotel for hotelgroup $hotelgroup and id $id found $hotelWithLocation -> returning 200 (with json body)")
        val (hotel, location) = hotelWithLocation
        val flightJson = new HotelRoomJson(hotel, location)
        Ok(toJson(flightJson))
      // bad case 1: no result
      case Vector() =>
        warn(s"No hotel for hotelgroup $hotelgroup and id $id -> returning 404")
        NotFound
      // error case 1: unexpected result
      case e =>
        error(s"unknown data for hotelgroup $hotelgroup and id $id $e returning 500")
        InternalServerError
    }
  }

  def book(hotelgroup: String, id: Int) = DBAction { implicit rs =>
    Ok("")
  }

  def cancel(hotelgroup: String, id: Int) = DBAction { implicit rs =>
    Ok("")
  }

}