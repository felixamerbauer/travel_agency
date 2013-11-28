package views.formdata

import scala.collection.mutable.Buffer
import play.api.data.validation.ValidationError
import models.Airline

case class AdminAirlineFormData(id: String = "-1", name: String = "", apiURL: String = "") {
  def this(airline: Airline) = this(
    id = airline.id.toString,
    name = airline.name,
    apiURL = airline.apiUrl)

  def validate(): Seq[ValidationError] = {
    val errors = Buffer[ValidationError]()
    null
  }
}
