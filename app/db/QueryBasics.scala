package db

import java.sql.Timestamp

import scala.slick.lifted.MappedTypeMapper
import scala.slick.lifted.Query

import org.joda.time.DateTime
import org.joda.time.LocalDate

import models.TAirline
import models.TCustomer
import models.THotelGroup
import models.TLocation
import models.TOrder
import models.TProduct
import models.TUser
import models.ext.TExtFlight
import models.ext.TExtFlightLastModified
import models.ext.TExtHotelRoom
import models.ext.TExtHotelRoomLastModified

object QueryBasics {

  implicit val dateTimeMapper = MappedTypeMapper.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime))

  implicit val localdateMapper = MappedTypeMapper.base[LocalDate, Timestamp](
    dt => new Timestamp(dt.toDateTimeAtStartOfDay().getMillis),
    ts => new LocalDate(ts.getTime))

  // Simple Queries
  val qAirline = Query(TAirline)
  val qCustomer = Query(TCustomer)
  val qHotelGroup = Query(THotelGroup)
  val qLocation = Query(TLocation)
  val qOrder = Query(TOrder)
  val qProduct = Query(TProduct)
  val qUser = Query(TUser)
  val qExtFlight = Query(TExtFlight)
  val qExtFlightLastModified = Query(TExtFlightLastModified)
  val qExtHotelRoom = Query(TExtHotelRoom)
  val qExtHotelRoomLastModified = Query(TExtHotelRoomLastModified)
}