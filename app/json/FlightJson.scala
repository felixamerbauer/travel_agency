package models.json

import org.joda.time.DateTime

import controllers.JsonHelper.isoDtf
import models.Location
import models.ext.ExtFlight

case class FlightJson(
  links: Seq[Link],
  from: String,
  to: String,
  dateTime: DateTime,
  availableSeats: Int,
  price: Int,
  currency: String) {
  def this(flight: ExtFlight, from: Location, to: Location) = {
    this(
      links = Seq(
        Link(
          rel = "self",
          href = s"http://127.0.0.1:9000/airline/${flight.apiUrl}/flights/${flight.id}"),
        Link(
          rel = "book",
          href = s"http://127.0.0.1:9000/airline/${flight.apiUrl}/flights/book/${flight.id}")),
      from = from.iataCode,
      to = to.iataCode,
      dateTime = flight.dateTime,
      availableSeats = flight.availableSeats,
      price = flight.price,
      currency = flight.currency)
  }

  lazy val pretty = s"$from - $to ${isoDtf.print(dateTime)} seats=$availableSeats price=$price $currency ${links(0).href}"
}
