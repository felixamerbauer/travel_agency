import play.api.db.slick.Config.driver.simple._
import db.QueryLibrary.qFlightsWithLocation
import models._

object Misc {
  lazy val db = Database.forURL("jdbc:postgresql://localhost:5432/travel_agency", driver = "org.postgresql.Driver", user = "travel_agency", password = "travel_agency")
}