package db

import db.QueryBasics.qExtFlight
import db.QueryBasics.qExtHotelRoom
import db.QueryBasics.qLocation
import play.api.db.slick.Config.driver.simple._

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

  def qFlight(airlineShortName: String, flightId: Int) = for {
    flight <- qExtFlight if (flight.id === flightId && flight.airlineShortName === airlineShortName)
  } yield flight

  def qHotelRoomsWithLocation = for {
    hotelroom <- qExtHotelRoom
    location <- qLocation if (location.id === hotelroom.locationId)
  } yield (hotelroom, location)

  def qHotelRoomsWithLocation(api: String, id: Int) = for {
    hotelroom <- qExtHotelRoom if (hotelroom.id === id && hotelroom.hotelShortName === api)
    location <- qLocation if (location.id === hotelroom.locationId)
  } yield (hotelroom, location)
}