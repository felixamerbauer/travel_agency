package json

case class FlightBookingResponse(links: Seq[Link]) extends BookingResponse {
  def this(apiUrl: String, bookingId: Int) = this(
    links = Seq(
      Link(
        rel = "cancel",
        href = s"http://127.0.0.1:9000/airline/$apiUrl/flights/cancel/$bookingId")))
}