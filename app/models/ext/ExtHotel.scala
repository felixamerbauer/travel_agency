package models.ext

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics._
import models.TLocation
import scala.slick.lifted.Projection7
import org.joda.time.DateMidnight
import db.Currency

object TExtHotel extends Table[ExtHotel]("exthotels") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def apiUrl = column[String]("apiurl")
  def hotelName = column[String]("hotelname")
  def description = column[String]("description")
  def category = column[Int]("category")
  def locationId = column[Int]("locationid")
  def startDate = column[DateMidnight]("startdate")
  def endDate = column[DateMidnight]("enddate")
  def availableRooms = column[Int]("availablerooms")
  def price = column[Int]("price")
  def currency = column[Currency]("currency")

  def baseProjection = apiUrl ~ hotelName ~ description ~ category ~ locationId ~ startDate ~ endDate ~ availableRooms ~ price ~ currency
  override def * = id ~: baseProjection <> (ExtHotel, ExtHotel.unapply _)
  def forInsert = baseProjection <> (
    { t => ExtHotel(-1, t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10) },
    { (p: ExtHotel) => Some((p.apiUrl, p.hotelName, p.description, p.category, p.locationId, p.startDate, p.endDate, p.availableRooms, p.price, p.currency)) })
  def autoInc = forInsert returning id

  def location = foreignKey("Location_FK", locationId, TLocation)(_.id)
}

case class ExtHotel(
  id: Int = -1,
  apiUrl: String,
  hotelName: String,
  description: String,
  category: Int,
  locationId: Int,
  startDate: DateMidnight,
  endDate: DateMidnight,
  availableRooms: Int,
  price: Int,
  currency: Currency)

  