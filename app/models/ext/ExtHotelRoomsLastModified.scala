package models.ext

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics.dateTimeMapper
import org.joda.time.DateTime
import models.TLocation

object TExtHotelRoomLastModified extends Table[ExtHotelRoomLastModified]("extHotelRoomsLastModified") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def lastModified = column[DateTime]("lastModified")
  def tmp = column[Option[Boolean]]("tmp")
  def baseProjection = lastModified ~ tmp
  override def * = id ~: baseProjection <> (ExtHotelRoomLastModified, ExtHotelRoomLastModified.unapply _)
  def forInsert = baseProjection <> (
    { t => ExtHotelRoomLastModified(-1, t._1) },
    { (p: ExtHotelRoomLastModified) => Some((p.lastModified, p.tmp)) })
  def autoInc = forInsert returning id
}

case class ExtHotelRoomLastModified(
  id: Int = -1,
  lastModified: DateTime,
  tmp: Option[Boolean] = None)

  