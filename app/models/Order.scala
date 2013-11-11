package models

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics.localdateMapper
import org.joda.time.DateMidnight

object TOrder extends Table[Order]("orders") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def customerId = column[Int]("customerid")
  def productId = column[Int]("productid")
  def hotelName = column[String]("hotelname")
  def hotelAddress = column[String]("hoteladdress")
  def personCount = column[Int]("personcount")
  def roomOrderId = column[String]("roomorderid")
  def toFlight = column[String]("toflight")
  def fromFlight = column[String]("fromflight")
  def startDate = column[DateMidnight]("startdate")
  def endDate = column[DateMidnight]("enddate")
  def price = column[Int]("price")
  def currency = column[String]("currency")
  def baseProjection = customerId ~ productId ~ hotelName ~ hotelAddress ~ personCount ~ roomOrderId ~ toFlight ~ fromFlight ~ startDate ~ endDate ~ price ~ currency
  override def * = id ~: baseProjection <> (Order, Order.unapply _)
  def forInsert = baseProjection <> (
    { t => Order(-1, t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12) },
    { (p: Order) => Some((p.customerId, p.productId, p.hotelName, p.hotelAddress, p.personCount, p.roomOrderId, p.toFlight, p.fromFlight, p.startDate, p.endDate, p.price, p.currency)) })
  def autoInc = forInsert returning id

  def customer = foreignKey("Customer_FK", customerId, TCustomer)(_.id)
  def product = foreignKey("Product_FK", productId, TProduct)(_.id)

}

case class Order(
  id: Int = -1,
  customerId: Int,
  productId: Int,
  hotelName: String,
  hotelAddress: String,
  personCount: Int,
  roomOrderId: String,
  toFlight: String,
  fromFlight: String,
  startDate: DateMidnight,
  endDate: DateMidnight,
  price: Int,
  currency: String)

  