package models

import play.api.db.slick.Config.driver.simple._

object TAirline extends Table[Airline]("airlines") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def apiUrl = column[String]("apiUrl")
  def baseProjection = name ~ apiUrl
  override def * = id ~: baseProjection <> (Airline, Airline.unapply _)
  def forInsert = baseProjection <> (
    { t => Airline(name = t._1, apiUrl = t._2) },
    { (p: Airline) => Some((p.name, p.apiUrl)) })
  def autoInc = forInsert returning id
}

case class Airline(
  id: Int = -1,
  name: String,
  apiUrl: String)

  