package db

import com.github.t3hnar.bcrypt.BCrypt
import com.github.t3hnar.bcrypt.Password
import db.QueryBasics._
import db.QueryLibrary._
import play.api.Logger.info
import play.api.Logger.warn
import play.api.db.slick.Config.driver.simple._
import models.ext._
import java.sql.SQLException
import models.Customer
import models.User

object QueryMethods {
  val defaultPassword = "password".bcrypt(BCrypt.gensalt())

  def bookFlightSeats(id: Int, requiredSeats: Int)(implicit session: Session): Option[Int] = session.withTransaction {
    val qFlight = for {
      flight <- qExtFlight.where(_.id === id)
    } yield (flight)
    qFlight.to[Vector] match {
      case Vector(flight) =>
        if (flight.availableSeats >= requiredSeats) {
          // 1) reduce number of seats
          val newAvailableSeats = flight.availableSeats - requiredSeats
          info(s"Enough seats available, ${flight.availableSeats} >= $requiredSeats, booking now (reducing available seats to $newAvailableSeats)")
          val qAvailableSeats = for {
            flight <- qExtFlight.where(_.id === id)
          } yield (flight.availableSeats)
          qAvailableSeats.update(newAvailableSeats)
          // 2) store booking
          val flightBooking = ExtFlightBooking(extFlightId = id, seats = requiredSeats)
          val flightBookingId = TExtFlightBooking.autoInc.insert(flightBooking)
          info("Inserted flight booking " + flightBooking.copy(id = flightBookingId))
          Some(flightBookingId)
        } else {
          warn(s"Sorry not enough seats ${flight.availableSeats} < $requiredSeats for flight $flight")
          None
        }
      case e =>
        warn("Unexpected result " + e)
        None
    }

  }

  def cancelFlightSeats(flight: ExtFlight, booking: ExtFlightBooking)(implicit session: Session): Boolean = session.withTransaction {
    try {
      val newAvailableSeats = flight.availableSeats + booking.seats
      info(s"New seats available, ${flight.availableSeats} + $booking.seats = $newAvailableSeats, cancelling now")
      qExtFlight.where(_.id === flight.id).map(_.availableSeats).update(newAvailableSeats)
      val rows = qExtFlightBooking.where(_.id === booking.id).delete
      if (rows != 1) throw new SQLException(s"Deleting booking failed (rows=$rows)")
      true
    } catch {
      case e: SQLException =>
        warn(s"Error while cancelling flight $flight and booking $booking")
        false
    }
  }

  def bookHotelRooms(id: Int, requiredRooms: Int)(implicit session: Session): Option[Int] = session.withTransaction {
    val qHotel = for {
      hotel <- qExtHotel.where(_.id === id)
    } yield (hotel)
    qHotel.to[Vector] match {
      case Vector(hotel) =>
        if (hotel.availableRooms >= requiredRooms) {
          val newAvailableRooms = hotel.availableRooms - requiredRooms
          info(s"Enough rooms available, ${hotel.availableRooms} >= $requiredRooms, booking now (reducing available seats to $newAvailableRooms)")
          val qAvailableRooms = for {
            hotel <- qExtHotel.where(_.id === id)
          } yield (hotel.availableRooms)
          qAvailableRooms.update(newAvailableRooms)
          // 2) store booking
          val hotelBooking = ExtHotelBooking(extHotelId = id, rooms = requiredRooms)
          val hotelBookingId = TExtHotelBooking.autoInc.insert(hotelBooking)
          info("Inserted hotel booking " + hotelBooking.copy(id = hotelBookingId))
          Some(hotelBookingId)
        } else {
          warn(s"Sorry not enough rooms ${hotel.availableRooms} < requiredRooms for hotel $hotel")
          None
        }
      case e =>
        warn("Unexpected result " + e)
        None
    }
  }

  def cancelHotelRooms(hotel: ExtHotel, booking: ExtHotelBooking)(implicit session: Session): Boolean = session.withTransaction {
    try {
      val newAvailableRooms = hotel.availableRooms + booking.rooms
      info(s"New rooms available, ${hotel.availableRooms} + ${booking.rooms} = $newAvailableRooms, cancelling now")
      qExtHotel.where(_.id === hotel.id).map(_.availableRooms).update(newAvailableRooms)
      val rows = qExtHotelBooking.where(_.id === booking.id).delete
      if (rows != 1) throw new SQLException(s"Deleting booking failed (rows=$rows)")
      true
    } catch {
      case e: SQLException =>
        warn(s"Error while cancelling flight $hotel and booking $booking")
        false
    }
  }

  def userCustomer(email: String)(implicit session: Session): Tuple2[User, Customer] = qUserWithCustomer.where(_._1.email === email).list.head

  def checkLogin(email: String, password: String)(implicit session: Session): Option[Tuple2[User,Customer]] = {
    // TODO hash
    qUserWithCustomer.list.find(e => e._1.email == email && e._1.passwordHash == password)
  }

}