package controllers

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import play.api.libs.json.Json.reads
import play.api.libs.json.Json.writes
import controllers.JsonHelper._
import models.json._
import models.Person
import models.flights.Flight

// order matters!
object JsonDeSerialization {
  // Json Models

  implicit val passwordReads = reads[Password]
  implicit val passwordWrites = writes[Password]

  implicit val loginReads = reads[Login]
  implicit val loginWrites = writes[Login]

  implicit val idReads = reads[Id]
  implicit val idWrites = writes[Id]

  implicit val flightReads = reads[Flight]
  implicit val flightWrites = writes[Flight]

}
