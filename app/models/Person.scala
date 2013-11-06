//package models
//
//import org.joda.time.LocalDate
//
//import db.QueryBasics.localdateMapper
//import models.RoleEnum.RoleEnum
//import play.api.db.slick.Config.driver.simple.Table
//import play.api.db.slick.Config.driver.simple.columnBaseToInsertInvoker
//
//object TPerson extends Table[Person]("person") {
//  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
//  def firstname = column[String]("firstname")
//  def lastname = column[String]("lastname")
//  def email = column[String]("email")
//  def password = column[String]("password")
//  def authToken = column[Option[String]]("authToken")
//  def birthday = column[Option[LocalDate]]("birthday")
//  def phone = column[Option[String]]("phone")
//  def baseProjection = firstname ~ lastname ~ email ~ password ~ authToken ~ birthday ~ phone
//  override def * = id ~: baseProjection <> (Person, Person.unapply _)
//  def forInsert = baseProjection <> (
//    { t => Person(-1, t._1, t._2, t._3, t._4, t._5, t._6, t._7) },
//    { (p: Person) => Some((p.firstname, p.lastname, p.email, p.password, p.authToken, p.birthday, p.phone)) })
//  def autoInc = forInsert returning id
//}
//
//case class Person(
//  id: Int = -1,
//  firstname: String,
//  lastname: String,
//  email: String,
//  password: String,
//  authToken: Option[String] = None,
//  birthday: Option[LocalDate],
//  phone: Option[String]) {
//  def fullname = firstname + " " + lastname
//}
//
//  