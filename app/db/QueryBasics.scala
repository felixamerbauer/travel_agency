package db

import java.sql.Timestamp
import org.joda.time.DateTime
import org.joda.time.LocalDate
import models._
import scala.slick.lifted.MappedTypeMapper
import scala.slick.lifted.Query

object QueryBasics {

  implicit val dateTimeMapper = MappedTypeMapper.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime))

  implicit val localdateMapper = MappedTypeMapper.base[LocalDate, Timestamp](
    dt => new Timestamp(dt.toDateTimeAtStartOfDay().getMillis),
    ts => new LocalDate(ts.getTime))

  // Simple Queries
//  val qPersons = Query(TPerson)
}