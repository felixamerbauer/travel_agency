package models.flights

import org.joda.time.LocalDate

import models.QueryBasics._
import models.QueryBasics.localdateMapper
import models.QueryBasics.personIdMapper
import models.RoleEnum.RoleEnum
import play.api.db.slick.Config.driver.simple.Table
import play.api.db.slick.Config.driver.simple.columnBaseToInsertInvoker
import play.api.Logger._

object TFlight extends Table[Flight]("flight") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def from = column[String]("from")
  def to = column[String]("to")
  def baseProjection = from ~ to
  override def * = id ~: baseProjection <> (Flight, Flight.unapply _)
  def forInsert = baseProjection <> (
    { t => Flight(from = t._1, to = t._2) },
    { (p: Flight) => Some((p.from, p.to)) })
  def autoInc = forInsert returning id
}

case class Flight(
  id: Int = -1,
  from: String,
  to: String)

object Continent extends Enumeration {
  type Continent = Value
  val Africa, America, Asia, Australia, Europe = Value
}

import Continent._

case class Airport(iata: String, city: String, country: String, continent: Continent)
object Airport {
  def unapply(iata: String): Option[Airport] = {
    info(s"unapply $iata")
    Airports.Airports.find(_.iata == iata)
  }
  def unapply(iata: Option[String]): Option[Airport] = iata.flatMap { iata =>
    info(s"unapply $iata")
    Airports.Airports.find(_.iata == iata)
  }
}  