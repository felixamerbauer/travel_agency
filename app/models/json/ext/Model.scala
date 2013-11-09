package models.json.ext

import org.joda.time.DateTime
import models.ext.ExtFlight
import models.Location

case class Link(rel: String, href: String)

case class FlightJson(
  links: Seq[Link],
  from: String,
  to: String,
  dateTime: DateTime,
  availableSeats: Int) {
  def this(flight: ExtFlight, from: Location, to: Location) = this(
    links = Seq(
      Link(
        rel = "self",
        href = s"http://127.0.0.1:9000/airline/${flight.airlineShortName}/flights/${flight.id}"),
      Link(
        rel = "book",
        href = s"http://127.0.0.1:9000/airline/${flight.airlineShortName}/flights/book/${flight.id}")),
    from = from.iataCode,
    to = to.iataCode,
    dateTime = flight.dateTime,
    availableSeats = flight.availableSeats)
}

case class FlightBookingDetails(seats: Int)
