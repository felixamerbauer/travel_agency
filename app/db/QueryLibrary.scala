package db

import db.QueryBasics.qExtFlight
import db.QueryBasics._
import db.QueryBasics.qLocation
import play.api.db.slick.Config.driver.simple._

object QueryLibrary {
  def qUserWithCustomer(userId: Int) = for {
    user <- qUser if (user.id === userId)
    customer <- qCustomer if (customer.userId === user.id)
  } yield (user, customer)

  val qUserWithCustomer = for {
    user <- qUser
    customer <- qCustomer if (customer.userId === user.id)
  } yield (user, customer)

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

  def qFlightBookingWithFlight(apiUrl: String, bookingId: Int) = for {
    booking <- qExtFlightBooking if (booking.id === bookingId)
    flight <- qExtFlight if (flight.apiUrl === apiUrl && flight.id === booking.extFlightId)
  } yield (booking, flight)

  def qHotel(apiUrl: String, hotelId: Int) = for {
    hotel <- qExtHotel if (hotel.id === hotelId && hotel.apiUrl === apiUrl)
  } yield hotel

  val qHotelWithLocation = for {
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

  def qHotelBookingWithHotel(apiUrl: String, bookingId: Int) = for {
    booking <- qExtHotelBooking if (booking.id === bookingId)
    hotel <- qExtHotel if (hotel.apiUrl === apiUrl && hotel.id === booking.extHotelId)
  } yield (booking, hotel)

  val qProductsWithLocation = for {
    product <- qProduct
    from <- qLocation if (from.id === product.fromLocationId)
    to <- qLocation if (to.id === product.toLocationId)
  } yield (product, from, to)

  val qActiveProductsOnlyLocationsIata = for {
    product <- qProduct if (product.archived === false)
    from <- qLocation if (from.id === product.fromLocationId)
    to <- qLocation if (to.id === product.toLocationId)
  } yield (from.iataCode, to.iataCode)

}