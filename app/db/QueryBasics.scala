package db

import java.sql.Timestamp

import scala.slick.lifted.MappedTypeMapper
import scala.slick.lifted.Query

import org.joda.time.DateMidnight
import org.joda.time.DateTime

import models.Female
import models.Male
import models.Sex
import models.TAirline
import models.TCustomer
import models.THotelgroup
import models.TLocation
import models.TOrder
import models.TProduct
import models.TUser
import models.ext.TExtFlight
import models.ext.TExtFlightBooking
import models.ext.TExtFlightLastModified
import models.ext.TExtHotel
import models.ext.TExtHotelBooking
import models.ext.TExtHotelLastModified

sealed trait Currency
case object Euro extends Currency
case object RenminbiYuan extends Currency
case object Dollar extends Currency

object QueryBasics {
  val Currencies = Seq(Euro, RenminbiYuan, Dollar)

  val sexesStringType = Map[String, Sex]("m" -> Male, "f" -> Female)
  val sexesTypeString = sexesStringType map (_.swap)

  implicit val sexTypeMapper = MappedTypeMapper.base[Sex, String](
    { sexesTypeString(_) },
    { sexesStringType(_) })

  val currenciesStringType = Map[String, Currency]("EUR" -> Euro, "CNY" -> RenminbiYuan, "USD" -> Dollar)
  val currenciesTypeString = currenciesStringType map (_.swap)

  implicit val currenciesTypeMapper = MappedTypeMapper.base[Currency, String](
    { currenciesTypeString(_) },
    { currenciesStringType(_) })

  implicit val dateTimeMapper = MappedTypeMapper.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime))

  implicit val localdateMapper = MappedTypeMapper.base[DateMidnight, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateMidnight(ts.getTime))

  // Simple Queries
  val qAirline = Query(TAirline)
  val qCustomer = Query(TCustomer)
  val qHotelgroup = Query(THotelgroup)
  val qLocation = Query(TLocation)
  val qOrder = Query(TOrder)
  val qProduct = Query(TProduct)
  val qUser = Query(TUser)
  val qExtFlight = Query(TExtFlight)
  val qExtFlightBooking = Query(TExtFlightBooking)
  val qExtFlightLastModified = Query(TExtFlightLastModified)
  val qExtHotel = Query(TExtHotel)
  val qExtHotelBooking = Query(TExtHotelBooking)
  val qExtHotelLastModified = Query(TExtHotelLastModified)
}