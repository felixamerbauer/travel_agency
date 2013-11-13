package json
import controllers.Client.baseUrl

case class FlightBookingResponse(links: Seq[Link]) extends BookingResponse {
  def this(apiUrl: String, bookingId: Int) = this(
    links = Seq(
      Link(
        rel = "cancel",
        href = s"$baseUrl/airline/$apiUrl/flights/cancel/$bookingId")))
}