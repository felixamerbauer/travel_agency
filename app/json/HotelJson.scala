package json

import org.joda.time.LocalDate
import controllers.JsonHelper.isoDtf
import models.Location
import models.ext.ExtHotel
import org.joda.time.DateMidnight
import org.joda.time.Duration
import controllers.Client.baseUrl
import db.Currency

case class HotelJson(
  links: Seq[Link],
  name: String,
  startDate: DateMidnight,
  endDate: DateMidnight,
  location: String,
  availableRooms: Int,
  price: Int,
  currency: Currency) {
  def this(hotel: ExtHotel, location: Location) = this(
    links = Seq(
      Link(
        rel = "self",
        href = s"$baseUrl/hotelgroup/${hotel.apiUrl}/hotels/${hotel.id}"),
      Link(
        rel = "book",
        href = s"$baseUrl/hotelgroup/${hotel.apiUrl}/hotels/book/${hotel.id}")),
    name = hotel.hotelName,
    startDate = hotel.startDate,
    location = location.iataCode,
    endDate = hotel.endDate,
    availableRooms = hotel.availableRooms,
    price = hotel.price,
    currency = hotel.currency)

  lazy val duration = new Duration(startDate, endDate)

  def pretty = s"$location ${isoDtf.print(startDate)} - ${isoDtf.print(endDate)} rooms=$availableRooms price=$price $currency ${links(0).href}"

}
 
