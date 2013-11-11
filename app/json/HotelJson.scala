package models.json

import org.joda.time.LocalDate
import controllers.JsonHelper.isoDtf
import models.Location
import models.ext.ExtHotel
import org.joda.time.DateMidnight

case class HotelJson(
  links: Seq[Link],
  startDate: DateMidnight,
  endDate: DateMidnight,
  location: String,
  availableRooms: Int,
  personCount: Int) {
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
    personCount = hotel.personCount)

  def pretty = s"$location ${isoDtf.print(startDate)} - ${isoDtf.print(endDate)} rooms=$availableRooms persons=$personCount"

}

