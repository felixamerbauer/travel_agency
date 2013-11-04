package models.json.flights

import org.joda.time.LocalDate
import models.QueryBasics._
import models.flights.Data._
import models.QueryBasics.localdateMapper
import models.QueryBasics.personIdMapper
import models.RoleEnum.RoleEnum
import play.api.db.slick.Config.driver.simple.Table
import play.api.db.slick.Config.driver.simple.columnBaseToInsertInvoker
import play.api.Logger._
import org.joda.time.DateTime
import models.flights.Flight

case class Link(rel: String, href: String)

case class FlightJson(
  links: Seq[Link],
  from: String,
  to: String,
  start: DateTime,
  end: DateTime) {
  def this(flight: Flight) = this(
    links = Seq(
      Link(
        rel = "self",
        href = s"http://127.0.0.1:9000/airline/austrian/flights/${flight.id}"),
      Link(
        rel = "book",
        href = s"http://127.0.0.1:9000/airline/austrian/flights/${flight.id}/book")),
    from = flight.from,
    to = flight.to,
    start = flight.start,
    end = flight.end)
}
