import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTimeZone
import models.flights.Airport
import play.api.db.slick.Config.driver.simple._

object Misc extends App {
  lazy val db = Database.forURL("jdbc:postgresql://localhost:5432/travel_agency", driver = "org.postgresql.Driver", user = "travel_agency", password = "travel_agency")
  //  val supported = Seq("2013-07-04T23:37:46.782Z", "2007-12-24T18:21:00.000Z")
  Seq("BER", "VIE", "AAA").foreach { e =>
    e match {
      case Airport(airport) => println(airport)
      case _ => println(s"unknown $e")
    }
  }
  val x = for {
    a <- Airport.unapply("BER")
    b <- Airport.unapply("VIE")
    c <- Airport.unapply("AAA")
    d <- Airport.unapply("BER")
  } yield (a, b, c, d)
  println(x)
}