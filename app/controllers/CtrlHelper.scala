package controllers

import play.api.db.slick.DBSessionRequest
import play.api.libs.json.JsValue
import play.api.libs.json.Json.fromJson
import play.api.libs.json.Reads
import play.api.mvc.AnyContent

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

}