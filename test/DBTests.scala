

import play.api.db.slick.Config.driver.simple._
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import db.QueryLibrary._
import db.QueryBasics._
import db.QueryMethods._
import models.TUser
import org.scalatest.Finders
import Misc.db

class DB1Tests extends FunSuite with BeforeAndAfter {

  db.withSession {
    println(TUser.ddl)
  }
}