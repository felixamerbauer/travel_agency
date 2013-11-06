package models

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics.localdateMapper

object TOrder extends Table[Order]("orders") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def customerId = column[Int]("customerId")
  def productId = column[Int]("productId")
  def hotelName = column[String]("hotelName")
  def hotelAddress = column[String]("hotelAddress")
  def personCount = column[Int]("personCount")
  def roomOrderId = column[String]("roomOrderId")
  def toFlight = column[String]("toFlight")
  def fromFlight = column[String]("fromFlight")
  def startDate = column[LocalDate]("startDate")
  def endDate = column[LocalDate]("endDate")
  def price = column[Double]("price")
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
  startDate: LocalDate,
  endDate: LocalDate,
  price: Double,
  currency: String)

  