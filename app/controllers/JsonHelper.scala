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
import db.QueryBasics.currenciesStringType
import db.QueryBasics.currenciesTypeString
import db.Currency
object JsonHelper {
  val isoDtf = ISODateTimeFormat.dateTimeNoMillis()

  // datetime
  implicit object currencyReads extends Reads[Currency] {
    override def reads(json: JsValue) = json match {
      case JsString(s) => JsSuccess(currenciesStringType(s))
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error parsing ISO 8601 datetime"))))
    }
  }

  implicit object currencyWrites extends Writes[Currency] {
    override def writes(currency: Currency) = JsString(currenciesTypeString(currency))
  }
  
  
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


}