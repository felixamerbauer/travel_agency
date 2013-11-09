package db

import com.github.t3hnar.bcrypt.BCrypt
import com.github.t3hnar.bcrypt.Password

import db.QueryBasics.qExtFlight
import play.api.Logger.info
import play.api.Logger.warn
import play.api.db.slick.Config.driver.simple.Session
import play.api.db.slick.Config.driver.simple.columnExtensionMethods
import play.api.db.slick.Config.driver.simple.productQueryToUpdateInvoker
import play.api.db.slick.Config.driver.simple.queryToQueryInvoker
import play.api.db.slick.Config.driver.simple.valueToConstColumn

object QueryMethods {
  val defaultPassword = "password".bcrypt(BCrypt.gensalt())

  def bookFlightSeats(id: Int, requiredSeats: Int)(implicit session: Session): Boolean = session.withTransaction {
    val qFlight = for {
      flight <- qExtFlight.where(_.id === id)
    } yield (flight)
    qFlight.to[Vector] match {
      case Vector(flight) =>
        if (flight.availableSeats >= requiredSeats) {
          val newAvailableSeats = flight.availableSeats - requiredSeats
          info("Enough seats available, ${flight.availableSeats} >= $requiredSeats, booking now (reducing available seats to $newAvailableSeats)")
          val qAvailableSeats = for {
            flight <- qExtFlight.where(_.id === id)
          } yield (flight.availableSeats)
          qAvailableSeats.update(newAvailableSeats)
          true
        } else {
          warn(s"Sorry not enough seats ${flight.availableSeats} < $requiredSeats for flight $flight")
          false
        }
      case e =>
        warn("Unexpected result " + e)
        false
    }

  }

  def cancelFlightSeats(id: Int, cancelledSeats: Int)(implicit session: Session): Boolean = session.withTransaction {
    val qFlight = for {
      flight <- qExtFlight.where(_.id === id)
    } yield (flight)
    qFlight.to[Vector] match {
      case Vector(flight) =>
        val newAvailableSeats = flight.availableSeats + cancelledSeats
        info("New seats available, ${flight.availableSeats} + $cancelledSeats = $newAvailableSeats, cancelling now")
        val qAvailableSeats = for {
          flight <- qExtFlight.where(_.id === id)
        } yield (flight.availableSeats)
        qAvailableSeats.update(newAvailableSeats)
        true
      case e =>
        warn("Unexpected result " + e)
        false
    }
  }
}