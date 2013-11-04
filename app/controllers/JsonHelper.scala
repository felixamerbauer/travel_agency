package controllers

import scala.math.BigDecimal.long2bigDecimal
import org.joda.time.DateTime
import org.joda.time.LocalDate
import models.PersonId
import models.RoleEnum
import models.RoleEnum.RoleEnum
import play.api.data.validation.ValidationError
import play.api.libs.json.JsError
import play.api.libs.json.JsNumber
import play.api.libs.json.JsPath
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Reads
import play.api.libs.json.Writes
import org.joda.time.format.ISODateTimeFormat

object JsonHelper {
  val isoDtf = ISODateTimeFormat.dateTimeNoMillis()


//  // person id
//  implicit object personIdReads extends Reads[PersonId] {
//    override def reads(json: JsValue): JsResult[PersonId] = json match {
//      case JsString(s) => JsSuccess(new PersonId(s.toInt))
//      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.eventid"))))
//    }
//  }
//
//  implicit object personIdWrites extends Writes[PersonId] {
//    override def writes(et: PersonId) = JsString(et.id.toString)
//  }
//
//  // role enum
//  implicit object roleReads extends Reads[RoleEnum] {
//    override def reads(json: JsValue): JsResult[RoleEnum] = json match {
//      case JsString(s) => s match {
//        case RoleEnum(role) => JsSuccess(role)
//      }
//      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.season"))))
//    }
//  }
//
//  implicit object roleWrites extends Writes[RoleEnum] {
//    override def writes(role: RoleEnum) = JsString(role.toString)
//  }

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