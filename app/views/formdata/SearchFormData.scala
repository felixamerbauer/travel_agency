package views.formdata

import scala.collection.mutable.Buffer
import play.api.data.validation.ValidationError
import org.joda.time.LocalDate
import views.formdata.Commons.dateFormat
import play.api.data.validation._
import play.api.Logger._

case class SearchFormData(
  from: String = "",
  to: String = "",
  start: String = dateFormat.print(LocalDate.now.plusDays(7)),
  end: String = dateFormat.print(LocalDate.now.plusDays(14)),
  adults: String = "",
  children: String = "",
  category: String = "") {
}

object SearchFormData {
  val constraints: Constraint[SearchFormData] = Constraint("constraints")({ data =>
    val errors = Buffer[ValidationError]()
    val start = dateFormat.parseDateTime(data.start)
    val end = dateFormat.parseDateTime(data.end)
    debug(s"start $start end $end")
    if (!end.isAfter(start)) errors += ValidationError("End before start")
    if (errors.isEmpty) Valid else Invalid(errors.toSeq)
  })
}

