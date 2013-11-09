package controllers.ext

import controllers.CtrlHelper
import play.api.Logger.info
import play.api.Play.current
import play.api.db.slick.DBAction
import play.api.mvc.Controller

object HotelsCtrl extends Controller with CtrlHelper {


  def list(hotelgroup: String, from: Option[String], to: Option[String], start: Option[String], end: Option[String]) = DBAction { implicit rs =>
    info(s"list hotelGroup:$hotelgroup, from=$from to=$to start=$start end=$end")
    Ok("")
  }

  def find(hotelgroup: String, id: Int) = DBAction { implicit rs =>
    Ok("")
  }

  def book(hotelgroup: String, id: Int) = DBAction { implicit rs =>
    Ok("")
  }

  def cancel(hotelgroup: String, id: Int) = DBAction { implicit rs =>
    Ok("")
  }

}