package db
import play.api.db.slick.Config.driver.simple._
import db.QueryBasics._
import models.ext.ExtFlight
import models.Location

object QueryLibrary {

  val qFlightsWithLocation = for {
    flight <- qExtFlight
    to <- qLocation if (to.id === flight.toLocationId)
    from <- qLocation if (from.id === flight.fromLocationId)
  } yield (flight, to, from)

  def qFlightsWithLocation(airlineShortName: String) = for {
    flight <- qExtFlight if (flight.airlineShortName === airlineShortName)
    to <- qLocation if (to.id === flight.toLocationId)
    from <- qLocation if (from.id === flight.fromLocationId)
  } yield (flight, to, from)

  def qFlightsWithLocation(airlineShortName: String, flightId: Int) = for {
    flight <- qExtFlight if (flight.id === flightId && flight.airlineShortName === airlineShortName)
    to <- qLocation if (to.id === flight.toLocationId)
    from <- qLocation if (from.id === flight.fromLocationId)
  } yield (flight, to, from)

}