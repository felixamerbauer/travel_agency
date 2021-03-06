package json

import org.joda.time.DateTime
import controllers.JsonHelper.isoDtf
import models.Location
import models.ext.ExtFlight
import controllers.Client.baseUrl
import db.Currency

case class FlightJson(
  links: Seq[Link],
  airline: String,
  id: Int,
  from: String,
  to: String,
  dateTime: DateTime,
  availableSeats: Int,
  price: Int,
  currency: Currency) {
  def this(flight: ExtFlight, from: Location, to: Location) = {
    this(
      links = Seq(
        Link(
          rel = "self",
          href = s"$baseUrl/airline/${flight.apiUrl}/flights/${flight.id}"),
        Link(
          rel = "book",
          href = s"$baseUrl/airline/${flight.apiUrl}/flights/book/${flight.id}")),
      airline = flight.airlineName,
      id = flight.id,
      from = from.iataCode,
      to = to.iataCode,
      dateTime = flight.dateTime,
      availableSeats = flight.availableSeats,
      price = flight.price,
      currency = flight.currency)
  }

  lazy val pretty = s"$from - $to ${isoDtf.print(dateTime)} seats=$availableSeats price=$price $currency ${links(0).href}"
}
