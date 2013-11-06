package models

import play.api.db.slick.Config.driver.simple._

object THotelGroup extends Table[HotelGroup]("hotelgroups") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def apiUrl = column[String]("apiUrl")
  def baseProjection = name ~ apiUrl
  override def * = id ~: baseProjection <> (HotelGroup, HotelGroup.unapply _)
  def forInsert = baseProjection <> (
    { t => HotelGroup(name = t._1, apiUrl = t._2) },
    { (p: HotelGroup) => Some((p.name, p.apiUrl)) })
  def autoInc = forInsert returning id
}

case class HotelGroup(
  id: Int = -1,
  name: String,
  apiUrl: String)

  