package json
import controllers.Client.baseUrl
case class HotelBookingResponse(links: Seq[Link]) extends BookingResponse {
  def this(apiUrl: String, bookingId: Int) = this(
    links = Seq(
      Link(
        rel = "cancel",
        href = s"$baseUrl/hotelgroup/$apiUrl/hotels/cancel/$bookingId")))
}