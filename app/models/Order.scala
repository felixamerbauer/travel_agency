package models

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics._
import org.joda.time.DateMidnight
import db.Currency

object TOrder extends Table[Order]("orders") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def customerId = column[Int]("customerid")
  def from = column[String]("from")
  def to = column[String]("to")
  def hotelName = column[String]("hotelname")
  def hotelId = column[Int]("hotelid")
  def outwardFlightAirline = column[String]("outwardflightairline")
  def outwardFlightId = column[Int]("outwardflightid")
  def inwardFlightAirline = column[String]("inwardflightairline")
  def inwardFlightId = column[Int]("inwardflightid")
  def adults = column[Int]("adults")
  def children = column[Int]("children")
  def startDate = column[DateMidnight]("startdate")
  def endDate = column[DateMidnight]("enddate")
  def price = column[Int]("price")
  def currency = column[Currency]("currency")
  def baseProjection = customerId ~ from ~ to ~ hotelName ~ hotelId ~ outwardFlightAirline ~ outwardFlightId ~ inwardFlightAirline ~ inwardFlightId ~ adults ~ children ~ startDate ~ endDate ~ price ~ currency
  override def * = id ~: baseProjection <> (Order, Order.unapply _)
  def forInsert = baseProjection <> (
    { t => Order(-1, t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15) },
    { (p: Order) => Some((p.customerId, p.from, p.to, p.hotelName, p.hotelId, p.outwardFlightAirline, p.outwardFlightId, p.inwardFlightAirline, p.inwardFlightId, p.adults, p.children, p.startDate,p.endDate, p.price, p.currency)) })
  def autoInc = forInsert returning id

  def customer = foreignKey("Customer_FK", customerId, TCustomer)(_.id)

}

case class Order(
  id: Int = -1,
  customerId: Int,
  from: String,
  to: String,
  hotelName: String,
  hotelId: Int,
  outwardFlightAirline: String,
  outwardFlightId: Int,
  inwardFlightAirline: String,
  inwardFlightId: Int,
  adults: Int,
  children: Int,
  startDate: DateMidnight,
  endDate: DateMidnight,
  price: Int,
  currency: Currency)

  