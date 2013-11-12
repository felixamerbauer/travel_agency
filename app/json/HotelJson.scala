package models.json

import org.joda.time.LocalDate
import controllers.JsonHelper.isoDtf
import models.Location
import models.ext.ExtHotel
import org.joda.time.DateMidnight
import org.joda.time.Duration

case class HotelJson(
  links: Seq[Link],
  startDate: DateMidnight,
  endDate: DateMidnight,
  location: String,
  availableRooms: Int,
  price: Int,
  currency: String) {
  def this(hotel: ExtHotel, location: Location) = this(
    links = Seq(
      Link(
        rel = "self",
        href = s"http://127.0.0.1:9000/hotelgroup/${hotel.apiUrl}/hotels/${hotel.id}"),
      Link(
        rel = "book",
        href = s"http://127.0.0.1:9000/hotelgroup/${hotel.apiUrl}/hotels/book/${hotel.id}")),
    startDate = hotel.startDate,
    location = location.iataCode,
    endDate = hotel.endDate,
    availableRooms = hotel.availableRooms,
    price = hotel.price,
    currency = hotel.currency)

  lazy val duration = new Duration(startDate, endDate)

  def pretty = s"$location ${isoDtf.print(startDate)} - ${isoDtf.print(endDate)} rooms=$availableRooms price=$price $currency ${links(0).href}"

}

