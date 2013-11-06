package models.ext

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics.localdateMapper
import models.TLocation

object TExtHotelRoom extends Table[ExtHotelRoom]("extHotelRooms") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def hotelShortName = column[String]("hotelShortName")
  def hotelName = column[String]("hotelName")
  def locationId = column[Int]("locationId")
  def startDate = column[LocalDate]("startDate")
  def endDate = column[LocalDate]("endDate")
  def personCount = column[Int]("personCount")
  def availableRooms = column[Int]("availableRooms")
  def baseProjection = hotelShortName ~ hotelName ~ locationId ~ startDate ~ endDate ~ personCount ~ availableRooms
  override def * = id ~: baseProjection <> (ExtHotelRoom, ExtHotelRoom.unapply _)
  def forInsert = baseProjection <> (
    { t => ExtHotelRoom(-1, t._1, t._2, t._3, t._4, t._5, t._6, t._7) },
    { (p: ExtHotelRoom) => Some((p.hotelShortName, p.hotelName, p.locationId, p.startDate, p.endDate, p.personCount, p.availableRooms)) })
  def autoInc = forInsert returning id

  def location = foreignKey("Location_FK", locationId, TLocation)(_.id)
}

case class ExtHotelRoom(
  id: Int = -1,
  hotelShortName: String,
  hotelName: String,
  locationId: Int,
  startDate: LocalDate,
  endDate: LocalDate,
  personCount: Int,
  availableRooms: Int)

  