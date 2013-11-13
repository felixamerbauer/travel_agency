package controllers

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import play.api.libs.json.Json.reads
import play.api.libs.json.Json.writes
import controllers.JsonHelper._
import json._
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

  implicit val hotelLocationReads = reads[HotelLocation]
  implicit val hotelLocationWrites = writes[HotelLocation]

  implicit val locationReads = reads[Location]
  implicit val locationWrites = writes[Location]

  implicit val hotelReads = reads[HotelJson]
  implicit val hotelWrites = writes[HotelJson]

  implicit val flightBookingRequestReads = reads[FlightBookingRequest]
  implicit val flightBookingRequestWrites = writes[FlightBookingRequest]

  implicit val hotelBookingRequestReads = reads[HotelBookingRequest]
  implicit val hotelBookingRequestWrites = writes[HotelBookingRequest]

  implicit val flightBookingResponseReads = reads[FlightBookingResponse]
  implicit val flightBookingResponseWrites = writes[FlightBookingResponse]

  implicit val hotelBookingResponseReads = reads[HotelBookingResponse]
  implicit val hotelBookingResponseWrites = writes[HotelBookingResponse]

}
