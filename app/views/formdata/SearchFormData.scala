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
  start: String = dateFormat.print(new LocalDate(2014, 2, 3)),
  end: String = dateFormat.print(new LocalDate(2014, 2, 10)),
  adults: Int = 2,
  children: Int = 0,
  category: String = "") {
}

object SearchFormData {
  val constraints: Constraint[SearchFormData] = Constraint("constraints")({ data =>
    val errors = Buffer[ValidationError]()
    val start = dateFormat.parseDateTime(data.start)
    val end = dateFormat.parseDateTime(data.end)
    debug(s"start $start end $end")
    if (!end.isAfter(start)) errors += ValidationError("Das Ende der Reise muss nach dem Anfang liegen.")
    if (errors.isEmpty) Valid else Invalid(errors.toSeq)
  })
}

