package models.ext

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics.dateTimeMapper
import org.joda.time.DateTime
import models.TLocation

object TExtHotelLastModified extends Table[ExtHotelLastModified]("exthotelslastmodified") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def lastModified = column[DateTime]("lastmodified")
  def tmp = column[Option[Boolean]]("tmp")
  def baseProjection = lastModified ~ tmp
  override def * = id ~: baseProjection <> (ExtHotelLastModified, ExtHotelLastModified.unapply _)
  def forInsert = baseProjection <> (
    { t => ExtHotelLastModified(-1, t._1) },
    { (p: ExtHotelLastModified) => Some((p.lastModified, p.tmp)) })
  def autoInc = forInsert returning id
}

case class ExtHotelLastModified(
  id: Int = -1,
  lastModified: DateTime,
  tmp: Option[Boolean] = None)

  