package controllers
import play.api.db.slick.Session
import models.Direction
import play.api.libs.ws.WS
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import db.QueryBasics._
import db.QueryLibrary._
import play.api.db.slick.Config.driver.simple._
import play.api.libs.json.Json
import controllers.JsonDeSerialization._
import play.api.libs.ws.Response
import play.api.Logger._
import models.Location
import org.joda.time.LocalDate

object Client {

  private def fetchApiUrls(implicit session: Session): Tuple2[Seq[String], Seq[String]] = {
    // fetch current airlines and 
    val airlineApiUrls = qAirline.map(_.apiUrl).to[Vector]
    val hotelgroupApiUrls = qHotelgroup.map(_.apiUrl).to[Vector]
    (airlineApiUrls, hotelgroupApiUrls)
  }

  private def get(url: String): Response = {
    debug(s"get $url")
    val call = WS.url(url).get()
    Await.result(call, 20.seconds)
  }

  def fetchDirections(implicit session: Session): Set[Direction] = {
    val (airlineApiUrls, hotelgroupApiUrls) = fetchApiUrls
    // airlines
    val directions = (for (airlineApiUrl <- airlineApiUrls) yield {
      val result = get(s"http://127.0.0.1:9000/airline/$airlineApiUrl/directions")
      Json.fromJson[Seq[Direction]](result.json).get
    }).flatten
    info(s"directions ${directions.size}")
    val directionsDistinct = directions.toSet
    info(s"directions distinct ${directionsDistinct.size}")
    // hotelgroups
    val locations = (for (hotelgroupApiUrl <- hotelgroupApiUrls) yield {
      val result = get(s"http://127.0.0.1:9000/hotelgroup/$hotelgroupApiUrl/locations")
      Json.fromJson[Seq[String]](result.json).get
    }).flatten
    info(s"locations ${locations.size}")
    val locationsDistinct = locations.toSet
    info(s"locations distinct ${locationsDistinct.size}")
    // we take only directions where there 
    // is a hotel available on at least one side of the direction
    // TODO and where a flight goes in both ways
    directionsDistinct.filter(direction => locations.exists(location => location == direction.from || location == direction.from))
  }

  def checkAvailability(location: String, start: LocalDate, end: LocalDate, durationMin: Int, durationMax: Int)(implicit session: Session) = {
    val (airlineApiUrls, hotelgroupApiUrls) = fetchApiUrls
    // airlines
//    val flights = (for (airlineApiUrl <- airlineApiUrls) yield {
//      val url = s"http://127.0.0.1:9000/airline/$airlineApiUrl/directions"
//      val result = get(s"")
//      Json.fromJson[Seq[Direction]](result.json).get
//    }).flatten
//    info(s"directions ${directions.size}")
//    val directionsDistinct = directions.toSet
//    info(s"directions distinct ${directionsDistinct.size}")
    // hotelgroups
//    val locations = (for (hotelgroupApiUrl <- hotelgroupApiUrls) yield {
//      val result = get(s"http://127.0.0.1:9000/hotelgroup/$hotelgroupApiUrl/locations")
//      Json.fromJson[Seq[String]](result.json).get
//    }).flatten
//    info(s"locations ${locations.size}")
//    val locationsDistinct = locations.toSet
//    info(s"locations distinct ${locationsDistinct.size}")
	  
  }

}