package models

import play.api.db.slick.Config.driver.simple._

object THotelgroup extends Table[Hotelgroup]("hotelgroups") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def apiUrl = column[String]("apiUrl")
  def baseProjection = name ~ apiUrl
  override def * = id ~: baseProjection <> (Hotelgroup, Hotelgroup.unapply _)
  def forInsert = baseProjection <> (
    { t => Hotelgroup(name = t._1, apiUrl = t._2) },
    { (p: Hotelgroup) => Some((p.name, p.apiUrl)) })
  def autoInc = forInsert returning id
}

case class Hotelgroup(
  id: Int = -1,
  name: String,
  apiUrl: String)

  