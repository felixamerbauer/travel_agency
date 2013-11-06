package models

import play.api.db.slick.Config.driver.simple._

object TLocation extends Table[Location]("locations") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def iataCode = column[String]("iataCode")
  def fullName = column[String]("fullName")
  def baseProjection = iataCode ~ fullName
  override def * = id ~: baseProjection <> (Location, Location.unapply _)
  def forInsert = baseProjection <> (
    { t => Location(-1, t._1, t._2) },
    { (p: Location) => Some((p.iataCode, p.fullName)) })
  def autoInc = forInsert returning id
  
}

case class Location(
  id: Int = -1,
  iataCode: String,
  fullName: String)

  