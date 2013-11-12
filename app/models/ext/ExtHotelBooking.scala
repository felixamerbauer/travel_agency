package models.ext

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics.dateTimeMapper
import org.joda.time.DateTime
import models.ext._

object TExtHotelBooking extends Table[ExtHotelBooking]("exthotelbookings") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def extHotelId = column[Int]("exthotelid")
  def rooms = column[Int]("rooms")
  def baseProjection = extHotelId ~ rooms
  override def * = id ~: baseProjection <> (ExtHotelBooking, ExtHotelBooking.unapply _)
  def forInsert = baseProjection <> (
    { t => ExtHotelBooking(-1, t._1, t._2) },
    { (p: ExtHotelBooking) => Some((p.extHotelId, p.rooms)) })
  def autoInc = forInsert returning id

  def fromLocation = foreignKey("ExtFlight_FK", extHotelId, TExtHotel)(_.id)
}

case class ExtHotelBooking(
  id: Int = -1,
  extHotelId: Int,
  rooms: Int)

  