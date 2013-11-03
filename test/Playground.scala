import play.api.db.slick.Config.driver.simple._

import models.QueryLibrary._

object Playground extends App {
  import play.api.db.slick.Config.driver.simple.Database.threadLocalSession

  lazy val db = Database.forURL("jdbc:mysql://localhost/fctorpedo03_201307", driver = "com.mysql.jdbc.Driver", user = "root", password = "****")

  db.withSession {
  }
}