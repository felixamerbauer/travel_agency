package db

import db.QueryBasics.qExtFlight
import db.QueryBasics._
import db.QueryBasics.qLocation
import play.api.db.slick.Config.driver.simple._

object QueryLibrary {

  val qFlightsWithLocation = for {
    flight <- qExtFlight
    to <- qLocation if (to.id === flight.toLocationId)
    from <- qLocation if (from.id === flight.fromLocationId)
  } yield (flight, to, from)

  def qFlightsWithLocation(apiUrl: String) = for {
    flight <- qExtFlight if (flight.apiUrl === apiUrl)
    to <- qLocation if (to.id === flight.toLocationId)
    from <- qLocation if (from.id === flight.fromLocationId)
  } yield (flight, to, from)

  def qFlightsWithLocation(apiUrl: String, flightId: Int) = for {
    flight <- qExtFlight if (flight.id === flightId && flight.apiUrl === apiUrl)
    to <- qLocation if (to.id === flight.toLocationId)
    from <- qLocation if (from.id === flight.fromLocationId)
  } yield (flight, to, from)

  def qFlight(apiUrl: String, flightId: Int) = for {
    flight <- qExtFlight if (flight.id === flightId && flight.apiUrl === apiUrl)
  } yield flight

  def qHotelWithLocation = for {
    hotel <- qExtHotel
    location <- qLocation if (location.id === hotel.locationId)
  } yield (hotel, location)

  def qHotelWithLocation(api: String) = for {
    hotel <- qExtHotel if (hotel.apiUrl === api)
    location <- qLocation if (location.id === hotel.locationId)
  } yield (hotel, location)
  
  def qHotelWithLocation(api: String, id: Int) = for {
    hotel <- qExtHotel if (hotel.id === id && hotel.apiUrl === api)
    location <- qLocation if (location.id === hotel.locationId)
  } yield (hotel, location)
}