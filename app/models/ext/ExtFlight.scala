package models.ext

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics._
import org.joda.time.DateTime
import models.TLocation
import db.Currency

object TExtFlight extends Table[ExtFlight]("extflights") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def apiUrl = column[String]("apiurl")
  def airlineName = column[String]("airlinename")
  def fromLocationId = column[Int]("fromlocationid")
  def toLocationId = column[Int]("tolocationid")
  def dateTime = column[DateTime]("datetime")
  def availableSeats = column[Int]("availableseats")
  def price = column[Int]("price")
  def currency = column[Currency]("currency")
  def baseProjection = apiUrl ~ airlineName ~ fromLocationId ~ toLocationId ~ dateTime ~ availableSeats ~ price ~ currency
  override def * = id ~: baseProjection <> (ExtFlight, ExtFlight.unapply _)
  def forInsert = baseProjection <> (
    { t => ExtFlight(-1, t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8) },
    { (p: ExtFlight) => Some((p.apiUrl, p.airlineName, p.fromLocationId, p.toLocationId, p.dateTime, p.availableSeats, p.price, p.currency)) })
  def autoInc = forInsert returning id

  def fromLocation = foreignKey("FromLocation_FK", fromLocationId, TLocation)(_.id)
  def toLocation = foreignKey("ToLocation_FK", toLocationId, TLocation)(_.id)
}

case class ExtFlight(
  id: Int = -1,
  apiUrl: String,
  airlineName: String,
  fromLocationId: Int,
  toLocationId: Int,
  dateTime: DateTime,
  availableSeats: Int,
  price: Int,
  currency: Currency)

  