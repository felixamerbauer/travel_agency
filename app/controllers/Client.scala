package controllers
import play.api.db.slick.Session
import json.Direction
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
import json._
import org.joda.time.Duration
import play.api.libs.json.JsValue

object Client {
  val baseUrl="http://127.0.0.1:9000"
  
  case class Journey(hotel: HotelJson, outward: FlightJson, inward: FlightJson, persons: Int, rooms: Int) {
    val price = (hotel.price + outward.price + inward.price) * 2
    lazy val pretty = s"${hotel.pretty} - ${outward.pretty} - ${inward.pretty} - persons=$persons rooms=$rooms"
  }

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
    info(s"fetchDirections")
    val (airlineApiUrls, hotelgroupApiUrls) = fetchApiUrls
    // airlines
    val directions = (for (airlineApiUrl <- airlineApiUrls) yield {
      val result = get(s"$baseUrl/airline/$airlineApiUrl/directions")
      Json.fromJson[Seq[Direction]](result.json).get
    }).flatten
    info(s"directions ${directions.size}")
    val directionsDistinct = directions.toSet
    info(s"directionsDistinct ${directionsDistinct.size}")
    // hotelgroups
    val locations = (for (hotelgroupApiUrl <- hotelgroupApiUrls) yield {
      val result = get(s"$baseUrl/hotelgroup/$hotelgroupApiUrl/locations")
      Json.fromJson[Seq[HotelLocation]](result.json).get
    }).flatten
    info(s"locations ${locations.size}")
    val locationsDistinct = locations.toSet
    info(s"locationsDistinct ${locationsDistinct.size}")
    // we take only directions where there 
    // is a hotel available on at least one side of the direction
    val directionsWithLocation = directionsDistinct.filter { direction =>
      locations.exists(location => location.location == direction.from || location.location == direction.to)
    }
    info(s"directionsWithLocation ${directionsWithLocation.size}")
    debug(directionsWithLocation.mkString("\n\t", "\n\t", ""))

    // and where a flight goes in both ways
    val directionsBothWays = directionsWithLocation.filter { d1 =>
      directionsWithLocation.exists(d2 => d2.from == d1.to && d2.to == d1.from)
    }
    info(s"directionsBothWays ${directionsBothWays.size}")

    // query product directions
    val productsDirections = qActiveProductsOnlyLocationsIata.map(e => (e._1, e._2)).to[Set]
      .map(e => Direction(e._1, e._2))
    info(s"productsDirections ${productsDirections.size}")
    debug(productsDirections.mkString("\n\t", "\n\t", ""))

    // intersect directions from flights/hotels with products
    val directionsResult = directionsBothWays.intersect(productsDirections)
    info(s"directionsResult ${directionsResult.size}")
    directionsResult
  }

  // TODO duration min/max would be nice
  def checkAvailability(from: String, location: String, start: DateMidnight, end: DateMidnight, persons: Int, rooms: Int)(implicit session: Session): Seq[Journey] = {
    info(s"checkAvailability from=$from location=$location start=$start end=$end persons=$persons rooms=$rooms")
    val (airlineApiUrls, hotelgroupApiUrls) = fetchApiUrls
    // Outward Flight
    val outwardFlights = (for (airlineApiUrl <- airlineApiUrls) yield {
      val queryParams = Seq(
        ("from", from),
        ("to", location),
        ("start", JsonHelper.isoDtf.print(start)),
        ("end", JsonHelper.isoDtf.print(start.toDateTime().plusDays(1).minus(1000))))
      val url = s"$baseUrl/airline/$airlineApiUrl/flights"
      val result = get(url, queryParams)
      Json.fromJson[Seq[FlightJson]](result.json).get
    }).flatten
    info(s"outwardFlights ${outwardFlights.size}")
    debug(outwardFlights.map(_.pretty).mkString("\n\t", "\n\t", ""))
    // Inward Flight
    val inwardFlights = (for (airlineApiUrl <- airlineApiUrls) yield {
      val queryParams = Seq(
        ("from", location),
        ("to", from),
        ("start", JsonHelper.isoDtf.print(end)),
        ("end", JsonHelper.isoDtf.print(end.toDateTime().plusDays(1).minus(1000))))
      val url = s"$baseUrl/airline/$airlineApiUrl/flights"
      val result = get(url, queryParams)
      Json.fromJson[Seq[FlightJson]](result.json).get
    }).flatten
    info(s"inwardFlights ${inwardFlights.size}")
    debug(inwardFlights.map(_.pretty).mkString("\n\t", "\n\t", ""))
    // Hotel
    val hotels = (for (hotelgroupApiUrl <- hotelgroupApiUrls) yield {
      val url = s"$baseUrl/hotelgroup/$hotelgroupApiUrl/hotels"
      val queryParams = Seq(
        ("location", location),
        ("start", JsonHelper.isoDtf.print(start)),
        ("end", JsonHelper.isoDtf.print(end)))
      val result = get(url, queryParams)
      Json.fromJson[Seq[HotelJson]](result.json).get
    }).flatten
    info(s"hotels ${hotels.size}")
    debug(hotels.map(_.pretty).mkString("\n\t", "\n\t", ""))
    // start with filtering hotels, that's easier
    val hotelsFiltered = hotels.filterNot { e =>
      e.availableRooms < rooms ||
        e.duration != new Duration(start, end)
    }
    info(s"hotelsFiltered ${hotelsFiltered.size}")
    debug(hotelsFiltered.map(_.pretty).mkString("\n\t", "\n\t", ""))
    // find matching flights for the hotels
    val packages = (hotelsFiltered.flatMap { h =>
      val outwardMatches = outwardFlights.filter { f =>
        f.to == h.location &&
          (f.dateTime.toDateMidnight() == h.startDate) &&
          f.availableSeats >= persons
      }
      val inwardMatches = inwardFlights.filter { f =>
        f.from == h.location &&
          f.dateTime.toDateMidnight() == h.endDate &&
          f.availableSeats >= persons
      }
      for {
        out <- outwardMatches
        in <- inwardMatches
      } yield Journey(h, out, in, persons, rooms)
    })
    info(s"packages ${packages.size}")
    debug(packages.map(_.pretty).mkString("\n\t", "\n\t", ""))
    packages
  }

  def book(journey: Journey): Tuple3[Option[FlightBookingResponse], Option[FlightBookingResponse], Option[HotelBookingResponse]] = {
    info(s"book $journey")
    def post(url: String, data: JsValue): Response = {
      debug(s"post url=$url data=$data")
      val call = WS.url(url).post(data)
      Await.result(call, 20.seconds)
    }

    def bookFlight(flight: FlightJson): Tuple2[Int, Option[FlightBookingResponse]] = {
      val url = flight.links.find(_.rel == "book").get.href
      val data = Json.toJson(FlightBookingRequest(seats = journey.persons))
      val result = post(url, data)
      info("Status " + result.status)
      if (result.status == 201) {
        (result.status, Some(Json.fromJson[FlightBookingResponse](result.json).get))
      } else (result.status, None)
    }

    def bookHotel(hotel: HotelJson): Tuple2[Int, Option[HotelBookingResponse]] = {
      val url = hotel.links.find(_.rel == "book").get.href
      val data = Json.toJson(HotelBookingRequest(rooms = journey.rooms))
      val result = post(url, data)
      info("Status " + result.status)
      if (result.status == 201) {
        (result.status, Some(Json.fromJson[HotelBookingResponse](result.json).get))
      } else (result.status, None)
    }

    // book outward flight
    val (outwardStatus, outwardResponse) = bookFlight(journey.outward)
    // book inward flight
    val (inwardStatus, inwardResponse) = bookFlight(journey.inward)
    // book hotel
    val (hotelStatus, hotelResponse) = bookHotel(journey.hotel)
    val success = Seq(outwardStatus, inwardStatus, hotelStatus).forall(_ == 201)
    info(s"Booking summary outward=$outwardStatus/outwardResponse inward=$inwardStatus/$inwardResponse hotel=$hotelStatus/$hotelResponse -> success=$success")
    (outwardResponse, inwardResponse, hotelResponse)
  }

  def cancel(bookings: Seq[BookingResponse]): Boolean = {
    info(s"cancel ${bookings.mkString(",")}")
    def delete(url: String): Response = {
      debug(s"delete url=$url")
      val call = WS.url(url).delete
      Await.result(call, 20.seconds)
    }

    val status = bookings.map { booking =>
      val url = booking.links.find(_.rel == "cancel").get.href
      val result = delete(url)
      info(s"Status $url -> ${result.status}")
      result.status
    }
    val success = status.forall(_ == 201)
    info(s"Cancel summary ${status.mkString("/")} -> success=$success")
    true
  }

}