package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.Config.driver.simple.Database.threadLocalSession
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import models.QueryLibrary._
import models.QueryBasics._
import models.QueryMethods._

class DBTests extends FunSuite with BeforeAndAfter {
  lazy val db = Database.forURL("jdbc:mysql://localhost/fctorpedo03_201307", driver = "com.mysql.jdbc.Driver", user = "root", password = "*****")

}