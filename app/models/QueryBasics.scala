package models

import java.sql.Timestamp

import org.joda.time.DateTime
import org.joda.time.LocalDate

import play.api.db.slick.Config.driver.simple.MappedTypeMapper
import play.api.db.slick.Config.driver.simple.Query

object QueryBasics {

  implicit val personIdMapper = MappedTypeMapper.base[PersonId, Int](_.id, new PersonId(_))
  val PersonIdNone = new PersonId(-1)

  implicit val dateTimeMapper = MappedTypeMapper.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime))

  implicit val localdateMapper = MappedTypeMapper.base[LocalDate, Timestamp](
    dt => new Timestamp(dt.toDateTimeAtStartOfDay().getMillis),
    ts => new LocalDate(ts.getTime))

  // Simple Queries
  val qPersons = Query(TPerson)
}