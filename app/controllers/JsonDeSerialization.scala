package controllers

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import play.api.libs.json.Json.reads
import play.api.libs.json.Json.writes
import controllers.JsonHelper._
import models.json._
import models.json.flights.FlightJson
import models.json.flights.Link

// order matters!
object JsonDeSerialization {
  // Json Models

  implicit val linkReads = reads[Link]
  implicit val linkWrites = writes[Link]

  implicit val flightReads = reads[FlightJson]
  implicit val flightWrites = writes[FlightJson]

}
