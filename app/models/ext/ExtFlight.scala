package models.ext

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics.dateTimeMapper
import org.joda.time.DateTime
import models.TLocation

object TExtFlight extends Table[ExtFlight]("extFlights") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def airlineShortName = column[String]("airlineShortName")
  // TODO should be foreign key to Airline
  def airlineName = column[String]("airlineName")
  def fromLocationId = column[Int]("fromLocationId")
  def toLocationId = column[Int]("toLocationId")
  def dateTime = column[DateTime]("dateTime")
  def availableSeats = column[Int]("availableSeats")
  def price = column[Double]("price")
  def baseProjection = airlineShortName ~ airlineName ~ fromLocationId ~ toLocationId ~ dateTime ~ availableSeats ~ price
  override def * = id ~: baseProjection <> (ExtFlight, ExtFlight.unapply _)
  def forInsert = baseProjection <> (
    { t => ExtFlight(-1, t._1, t._2, t._3, t._4, t._5, t._6, t._7) },
    { (p: ExtFlight) => Some((p.airlineShortName, p.airlineName, p.fromLocationId, p.toLocationId, p.dateTime, p.availableSeats, p.price)) })
  def autoInc = forInsert returning id

  def fromLocation = foreignKey("FromLocation_FK", fromLocationId, TLocation)(_.id)
  def toLocation = foreignKey("ToLocation_FK", toLocationId, TLocation)(_.id)
}

case class ExtFlight(
  id: Int = -1,
  airlineShortName: String,
  airlineName: String,
  fromLocationId: Int,
  toLocationId: Int,
  dateTime: DateTime,
  availableSeats: Int,
  price: Double)

  