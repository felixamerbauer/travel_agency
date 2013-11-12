package models.ext

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics.dateTimeMapper
import org.joda.time.DateTime
import models.ext._

object TExtFlightBooking extends Table[ExtFlightBooking]("extflightbookings") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def extFlightId = column[Int]("extflightid")
  def seats = column[Int]("seats")
  def baseProjection = extFlightId ~ seats
  override def * = id ~: baseProjection <> (ExtFlightBooking, ExtFlightBooking.unapply _)
  def forInsert = baseProjection <> (
    { t => ExtFlightBooking(-1, t._1, t._2) },
    { (p: ExtFlightBooking) => Some((p.extFlightId, p.seats)) })
  def autoInc = forInsert returning id

  def fromLocation = foreignKey("ExtFlight_FK", extFlightId, TExtFlight)(_.id)
}

case class ExtFlightBooking(
  id: Int = -1,
  extFlightId: Int,
  seats: Int)

  