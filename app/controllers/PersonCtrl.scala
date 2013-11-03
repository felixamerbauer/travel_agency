package controllers

import play.api.mvc.Controller
import play.api.db.slick.DBAction
import play.api.Play.current

object PersonCtrl extends Controller with CtrlHelper {

  def list = DBAction { implicit rs =>
    Ok("")
  }

  def find(id: Int) = DBAction { implicit rs =>
    Ok("")
  }

  def remove(id: Int) = DBAction { implicit rs =>
    Ok("")
  }

  def save(id: Int) = DBAction { implicit rs =>
    Ok("")
  }

  def create = DBAction { implicit rs =>
    Ok("")
  }
}