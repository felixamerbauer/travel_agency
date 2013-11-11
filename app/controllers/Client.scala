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
import org.joda.time.DateMidnight
import models.json._

object Client {

  private def fetchApiUrls(implicit session: Session): Tuple2[Seq[String], Seq[String]] = {
    // fetch current airlines and 
    val airlineApiUrls = qAirline.map(_.apiUrl).to[Vector]
    val hotelgroupApiUrls = qHotelgroup.map(_.apiUrl).to[Vector]
    (airlineApiUrls, hotelgroupApiUrls)
  }

  private def get(url: String, query: Seq[Tuple2[String, String]] = Seq()): Response = {
    debug(s"get url=$url${query.map(e => s"${e._1}=${e._2}").mkString("?", "&", "")}")
    val call = WS.url(url).withQueryString(query: _*).get()
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

  def checkAvailability(location: String, start: DateMidnight, end: DateMidnight, durationMin: Int, durationMax: Int)(implicit session: Session) = {
    val (airlineApiUrls, hotelgroupApiUrls) = fetchApiUrls
    // Outward Flight
    val outwardFlights = (for (airlineApiUrl <- airlineApiUrls) yield {
      val queryParams = Seq(
        ("to", location),
        ("start", JsonHelper.isoDtf.print(start)),
        ("end", JsonHelper.isoDtf.print(end)))
      val url = s"http://127.0.0.1:9000/airline/$airlineApiUrl/flights"
      val result = get(url, queryParams)
      Json.fromJson[Seq[FlightJson]](result.json).get
    }).flatten
    info(s"outwardFlights ${outwardFlights.size}")
    debug(outwardFlights.map(_.pretty).mkString("\n\t", "\n\t", ""))
    // Inward Flight
    val inwardFlights = (for (airlineApiUrl <- airlineApiUrls) yield {
      val queryParams = Seq(
        ("from", location),
        ("start", JsonHelper.isoDtf.print(start)),
        ("end", JsonHelper.isoDtf.print(end)))
      val url = s"http://127.0.0.1:9000/airline/$airlineApiUrl/flights"
      val result = get(url, queryParams)
      Json.fromJson[Seq[FlightJson]](result.json).get
    }).flatten
    info(s"inwardFlights ${inwardFlights.size}")
    debug(inwardFlights.map(_.pretty).mkString("\n\t", "\n\t", ""))
    // Hotel
    val locations = (for (hotelgroupApiUrl <- hotelgroupApiUrls) yield {
      val url = s"http://127.0.0.1:9000/hotelgroup/$hotelgroupApiUrl/hotels"
      val queryParams = Seq(
        ("location", location),
        ("start", JsonHelper.isoDtf.print(start)),
        ("end", JsonHelper.isoDtf.print(end)))
      val result = get(url, queryParams)
      Json.fromJson[Seq[HotelJson]](result.json).get
    }).flatten
    info(s"locations ${locations.size}")
    debug(locations.map(_.pretty).mkString("\n\t", "\n\t", ""))
  }

}