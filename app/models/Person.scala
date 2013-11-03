package models

import org.joda.time.LocalDate

import models.QueryBasics.PersonIdNone
import models.QueryBasics.localdateMapper
import models.QueryBasics.personIdMapper
import models.RoleEnum.RoleEnum
import play.api.db.slick.Config.driver.simple.Table
import play.api.db.slick.Config.driver.simple.columnBaseToInsertInvoker

class PersonId(val id: Int) extends AnyVal {
  override def toString = id.toString
}

object TPerson extends Table[Person]("person") {
  def id = column[PersonId]("id", O.PrimaryKey, O.AutoInc)
  def firstname = column[String]("firstname")
  def lastname = column[String]("lastname")
  def email = column[String]("email")
  def password = column[String]("password")
  def authToken = column[Option[String]]("authToken")
  def birthday = column[Option[LocalDate]]("birthday")
  def phone = column[Option[String]]("phone")
  def baseProjection = firstname ~ lastname ~ email ~ password ~ authToken ~ birthday ~ phone
  override def * = id ~: baseProjection <> (Person, Person.unapply _)
  def forInsert = baseProjection <> (
    { t => Person(PersonIdNone, t._1, t._2, t._3, t._4, t._5, t._6, t._7) },
    { (p: Person) => Some((p.firstname, p.lastname, p.email, p.password, p.authToken, p.birthday, p.phone)) })
  def autoInc = forInsert returning id
}

case class Person(
  id: PersonId = PersonIdNone,
  firstname: String,
  lastname: String,
  email: String,
  password: String,
  authToken: Option[String] = None,
  birthday: Option[LocalDate],
  phone: Option[String]) {
  def fullname = firstname + " " + lastname
}

  