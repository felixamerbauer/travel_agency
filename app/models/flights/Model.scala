package models.flights

import org.joda.time.LocalDate

import db.QueryBasics._
import models.flights.Data._
import db.QueryBasics.localdateMapper
import models.RoleEnum.RoleEnum
import play.api.db.slick.Config.driver.simple.Table
import play.api.db.slick.Config.driver.simple.columnBaseToInsertInvoker
import play.api.Logger._
import org.joda.time.DateTime

object TFlight extends Table[Flight]("flight") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def from = column[String]("from")
  def to = column[String]("to")
  def start = column[DateTime]("start")
  def end = column[DateTime]("end")
  def baseProjection = from ~ to ~ start ~ end
  override def * = id ~: baseProjection <> (Flight, Flight.unapply _)
  def forInsert = baseProjection <> (
    { t => Flight(from = t._1, to = t._2, start = t._3, end = t._4) },
    { (p: Flight) => Some((p.from, p.to, p.start, p.end)) })
  def autoInc = forInsert returning id
}

case class Direction(from: Airport, to: Airport)

case class Window(start: DateTime, end: DateTime)

case class Flight(
  id: Int = -1,
  from: String,
  to: String,
  start: DateTime,
  end: DateTime)

object Continent extends Enumeration {
  type Continent = Value
  val Africa, America, Asia, Australia, Europe = Value
}

import Continent._

case class Airport(iata: String, city: String, country: String, continent: Continent)

object Airport {
  def unapply(iata: String): Option[Airport] = {
    info(s"unapply $iata")
    Airports.find(_.iata == iata)
  }
  def unapply(iata: Option[String]): Option[Airport] = iata.flatMap { iata =>
    info(s"unapply $iata")
    Airports.find(_.iata == iata)
  }
}  