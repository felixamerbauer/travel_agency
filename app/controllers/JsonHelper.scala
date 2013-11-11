package controllers

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.data.validation.ValidationError
import play.api.libs.json.JsError
import play.api.libs.json.JsPath
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import org.joda.time.DateMidnight

object JsonHelper {
  val isoDtf = ISODateTimeFormat.dateTimeNoMillis()

  // datetime
  implicit object dateTimeReads extends Reads[DateTime] {
    override def reads(json: JsValue) = json match {
      case JsString(s) => JsSuccess(isoDtf.parseDateTime(s))
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error parsing ISO 8601 datetime"))))
    }
  }

  implicit object dateTimeWrites extends Writes[DateTime] {
    override def writes(dt: DateTime) = JsString(isoDtf.print(dt))
  }

  // dateMidnight
  implicit object dateMidnightReads extends Reads[DateMidnight] {
    override def reads(json: JsValue) = json match {
      case JsString(s) => JsSuccess(isoDtf.parseDateTime(s).toDateMidnight())
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error parsing ISO 8601 datetime"))))
    }
  }

  implicit object dateMidnightWrites extends Writes[DateMidnight] {
    override def writes(dt: DateMidnight) = JsString(isoDtf.print(dt))
  }

//  implicit object localDateReads extends Reads[LocalDate] {
//    override def reads(json: JsValue) = json match {
//      case JsNumber(s) => JsSuccess(new LocalDate(s.toLong))
//      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.jsstring"))))
//    }
//  }
//
//  implicit object localDateWrites extends Writes[LocalDate] {
//    override def writes(dt: LocalDate) = JsNumber(dt.toDateTimeAtStartOfDay().getMillis())
//  }

}