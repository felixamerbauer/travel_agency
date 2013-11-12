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
import org.joda.time.Duration
import play.api.libs.json.JsValue

object Client {
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
      val result = get(s"http://127.0.0.1:9000/airline/$airlineApiUrl/directions")
      Json.fromJson[Seq[Direction]](result.json).get
    }).flatten
    info(s"directions ${directions.size}")
    val directionsDistinct = directions.toSet
    info(s"directionsDistinct ${directionsDistinct.size}")
    // hotelgroups
    val locations = (for (hotelgroupApiUrl <- hotelgroupApiUrls) yield {
      val result = get(s"http://127.0.0.1:9000/hotelgroup/$hotelgroupApiUrl/locations")
      Json.fromJson[Seq[String]](result.json).get
    }).flatten
    info(s"locations ${locations.size}")
    val locationsDistinct = locations.toSet
    info(s"locationsDistinct ${locationsDistinct.size}")
    // we take only directions where there 
    // is a hotel available on at least one side of the direction
    val directionsWithLocation = directionsDistinct.filter { direction =>
      locations.exists(location => location == direction.from || location == direction.to)
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
        ("to", from),
        ("start", JsonHelper.isoDtf.print(end)),
        ("end", JsonHelper.isoDtf.print(end.toDateTime().plusDays(1).minus(1000))))
      val url = s"http://127.0.0.1:9000/airline/$airlineApiUrl/flights"
      val result = get(url, queryParams)
      Json.fromJson[Seq[FlightJson]](result.json).get
    }).flatten
    info(s"inwardFlights ${inwardFlights.size}")
    debug(inwardFlights.map(_.pretty).mkString("\n\t", "\n\t", ""))
    // Hotel
    val hotels = (for (hotelgroupApiUrl <- hotelgroupApiUrls) yield {
      val url = s"http://127.0.0.1:9000/hotelgroup/$hotelgroupApiUrl/hotels"
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

  def book(journey: Journey): Boolean = {
    info(s"book $journey")
    def post(url: String, data: JsValue): Response = {
      debug(s"post url=$url data=$data")
      val call = WS.url(url).post(data)
      Await.result(call, 20.seconds)
    }

    def bookFlight(flight: FlightJson) = {
      val url = flight.links.find(_.rel == "book").get.href
      val data = Json.toJson(FlightBookingDetails(seats = journey.persons))
      val result = post(url, data)
      info("Status " + result.status)
      result.status
    }

    def bookHotel(hotel: HotelJson) = {
      val url = hotel.links.find(_.rel == "book").get.href
      val data = Json.toJson(HotelBookingDetails(rooms = journey.rooms))
      val result = post(url, data)
      info("Status " + result.status)
      result.status
    }

    // book outward flight
    val outward = bookFlight(journey.outward)
    // book inward flight
    val inward = bookFlight(journey.inward)
    // book hotel
    val hotel = bookHotel(journey.hotel)
    val success = Seq(outward, inward, hotel).forall(_ == 201)
    info(s"Booking summary outward=$outward inward=$inward hotel=$hotel -> success=$success")
    success
  }

  def cancel(journey: Journey): Boolean = {
    info(s"cancel $journey")
    def delete(url: String): Response = {
      debug(s"delete url=$url")
      val call = WS.url(url).delete
      Await.result(call, 20.seconds)
    }

    def cancelFlight(flight: FlightJson) = {
      val url = flight.links.find(_.rel == "book").get.href
      val result = delete(url)
      info("Status " + result.status)
      result.status
    }

    def cancelHotel(hotel: HotelJson) = {
      val url = hotel.links.find(_.rel == "book").get.href
      val result = delete(url)
      info("Status " + result.status)
      result.status
    }

    // cancel outward flight
    val outward = cancelFlight(journey.outward)
    // cancel inward flight
    val inward = cancelFlight(journey.inward)
    // cancel hotel
    val hotel = cancelHotel(journey.hotel)
    val success = Seq(outward, inward, hotel).forall(_ == 201)
    info(s"Cancel summary outward=$outward inward=$inward hotel=$hotel -> success=$success")
    success
  }

}