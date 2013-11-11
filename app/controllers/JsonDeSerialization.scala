package controllers

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import play.api.libs.json.Json.reads
import play.api.libs.json.Json.writes
import controllers.JsonHelper._
import models.json._
import models.json.ext._
import models.Direction
import models.Location

// order matters!
object JsonDeSerialization {
  // Json Models

  implicit val linkReads = reads[Link]
  implicit val linkWrites = writes[Link]

  implicit val flightReads = reads[FlightJson]
  implicit val flightWrites = writes[FlightJson]

  implicit val directionReads = reads[Direction]
  implicit val directionWrites = writes[Direction]

  implicit val locationReads = reads[Location]
  implicit val locationWrites = writes[Location]

  implicit val hotelReads = reads[HotelJson]
  implicit val hotelWrites = writes[HotelJson]

  implicit val flightBookingDetailsReads = reads[FlightBookingDetails]
  implicit val flightBookingDetailsWrites = writes[FlightBookingDetails]

}
