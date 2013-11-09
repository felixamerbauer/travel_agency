package controllers

import play.api.db.slick.DBSessionRequest
import play.api.libs.json.JsValue
import play.api.libs.json.Json.fromJson
import play.api.libs.json.Reads
import play.api.mvc.AnyContent
import play.api.Logger._
import controllers.JsonHelper.isoDtf
import org.joda.time.DateTime
import org.joda.time.LocalDate

trait CtrlHelper {
  def parse[T](implicit rs: DBSessionRequest[AnyContent], fjs: Reads[T]): T = {
    // TODO handle exceptions
    val json: JsValue = rs.request.body.asJson.get
    fromJson[T](json).get
  }
  //  def parseAuth[T](implicit urs: AuthenticatedRequest, fjs: Reads[T]): T = {
  //    val json: JsValue = urs.rs.request.body.asJson.get
  //    fromJson[T](json).get
  //  }
  def parseDateTime(dateTime: String): Option[DateTime] = try {
    Some(isoDtf.parseDateTime(dateTime))
  } catch {
    case e: IllegalArgumentException =>
      warn(s"unknown iso date time $dateTime", e)
      None
  }

  def parseLocatDate(dateTime: String): Option[LocalDate] = try {
    Some(isoDtf.parseLocalDate(dateTime))
  } catch {
    case e: IllegalArgumentException =>
      warn(s"unknown iso local date $dateTime", e)
      None
  }

}