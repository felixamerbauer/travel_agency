package models

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics.dateTimeMapper
import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics._
import org.joda.time.DateTime

object TCustomer extends Table[Customer]("customers") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Int]("user")
  def firstName = column[String]("firstName")
  def lastName = column[String]("lastName")
  def birthDate = column[LocalDate]("birthDate")
  def sex = column[String]("sex")
  def street = column[String]("street")
  def zipCode = column[String]("zipCode")
  def city = column[String]("city")
  def country = column[String]("country")
  def phoneNumber = column[String]("phoneNumber")
  def creditCardCompany = column[String]("creditCardCompany")
  def creditCardNumber = column[String]("creditCardNumber")
  def creditCardExpireDate = column[String]("creditCardExpireDate")
  def creditCardVerificationCode = column[String]("creditCardVerificationCode")
  def baseProjection = userId ~ firstName ~ lastName ~ birthDate ~ sex ~ street ~ zipCode ~ city ~ country ~ phoneNumber ~ creditCardCompany ~ creditCardNumber ~ creditCardExpireDate ~ creditCardVerificationCode
  override def * = id ~: baseProjection <> (Customer, Customer.unapply _)
  def forInsert = baseProjection <> (
    { t => Customer(-1, t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14) },
    { (p: Customer) => Some((p.userId, p.firstName, p.lastName, p.birthDate, p.sex, p.street, p.zipCode, p.city, p.country, p.phoneNumber, p.creditCardCompany, p.creditCardNumber, p.creditCardExpireDate, p.creditCardVerificationCode)) })
  def autoInc = forInsert returning id

  def user = foreignKey("User_FK", userId, TUser)(_.id)
}

case class Customer(
  id: Int = -1,
  userId: Int,
  firstName: String,
  lastName: String,
  birthDate: LocalDate,
  sex: String,
  street: String,
  zipCode: String,
  city: String,
  country: String,
  phoneNumber: String,
  creditCardCompany: String,
  creditCardNumber: String,
  creditCardExpireDate: String,
  creditCardVerificationCode: String)

  