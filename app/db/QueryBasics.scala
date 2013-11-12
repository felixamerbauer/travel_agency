package db

import java.sql.Timestamp
import scala.slick.lifted.MappedTypeMapper
import scala.slick.lifted.Query
import org.joda.time.DateTime
import org.joda.time.LocalDate
import models.TAirline
import models.TCustomer
import models.THotelgroup
import models.TLocation
import models.TOrder
import models.TProduct
import models.TUser
import models.ext.TExtFlight
import models.ext.TExtFlightLastModified
import models.ext._
import org.joda.time.DateMidnight

object QueryBasics {

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