package models

import play.api.db.slick.Config.driver.simple._

object TUser extends Table[User]("users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email")
  def passwordHash = column[String]("passwordHash")
  def baseProjection = email ~ passwordHash
  override def * = id ~: baseProjection <> (User, User.unapply _)
  def forInsert = baseProjection <> (
    { t => User(email = t._1, passwordHash = t._2) },
    { (p: User) => Some((p.email, p.passwordHash)) })
  def autoInc = forInsert returning id

  def emailUnique = index("users_email_UNIQUE", email, unique = true)
}

case class User(
  id: Int = -1,
  email: String,
  passwordHash: String)

  