package models

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics.dateTimeMapper
import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate
import db.QueryBasics._
import org.joda.time.DateTime
import org.joda.time.DateMidnight

sealed trait Sex
case object Male extends Sex
case object Female extends Sex

  
object TCustomer extends Table[Customer]("customers") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Int]("userid")
  def firstName = column[String]("firstname")
  def lastName = column[String]("lastname")
  def birthDate = column[DateMidnight]("birthdate")
  def sex = column[Sex]("sex")
  def street = column[String]("street")
  def zipCode = column[String]("zipcode")
  def city = column[String]("city")
  def country = column[String]("country")
  def phoneNumber = column[String]("phonenumber")
  def creditCardCompany = column[String]("creditcardcompany")
  def creditCardNumber = column[String]("creditcardnumber")
  def creditCardExpireDate = column[DateMidnight]("creditcardexpiredate")
  def creditCardVerificationCode = column[String]("creditcardverificationcode")
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
  birthDate: DateMidnight,
  sex: Sex,
  street: String,
  zipCode: String,
  city: String,
  country: String,
  phoneNumber: String,
  creditCardCompany: String,
  creditCardNumber: String,
  creditCardExpireDate: DateMidnight,
  creditCardVerificationCode: String)

  