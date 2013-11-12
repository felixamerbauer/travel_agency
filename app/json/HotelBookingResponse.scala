package json

case class HotelBookingResponse(links: Seq[Link]) extends BookingResponse {
  def this(apiUrl: String, bookingId: Int) = this(
    links = Seq(
      Link(
        rel = "cancel",
        href = s"http://127.0.0.1:9000/hotelgroup/$apiUrl/hotels/cancel/$bookingId")))
}