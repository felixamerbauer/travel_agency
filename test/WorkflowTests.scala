import scala.concurrent.ExecutionContext
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import Misc.db
import controllers.Client
import play.api.db.slick.Config.driver.simple.Database.threadLocalSession
import play.api.test.FakeApplication
import play.api.test.Helpers.running
import org.joda.time.LocalDate
import org.joda.time.DateMidnight
import json.BookingResponse

class WorkflowTests extends FunSuite with BeforeAndAfter {
  implicit val context = ExecutionContext.Implicits.global

  implicit def tuple3ToDateMidnight(t: Tuple3[Int, Int, Int]): DateMidnight = {
    val (year, month, day) = t
    new DateMidnight(year, month, day)
  }

  test("complete workflow") {
    running(FakeApplication()) {

      db.withSession {
        val directions = Client.fetchDirections
        println(s"directions\n\t${directions.mkString("\n\t")}")
        // choose first
        val chosenDirection = directions.head
        println(s"chosen direction $chosenDirection")

        val journeys = Client.checkAvailability(chosenDirection.from, chosenDirection.to, (2013, 12, 24), (2013, 12, 26), rooms = 1, persons = 2)
        println(s"packages\n\t${journeys.map(_.pretty).mkString("\n\t")}")
        // choose cheapest
        val chosenJourney = journeys.minBy(_.price)
        println(s"chosen journey ${chosenJourney.pretty}")

        val (bookingInward,bookingOutward,bookingHotel) = Client.book(chosenJourney)
        val bookings:Seq[Option[BookingResponse]]=Seq(bookingInward,bookingOutward,bookingHotel)
        println(s"bookingOutward=$bookingOutward, bookingInward=$bookingInward, bookinHotel=$bookingHotel")

        val cancellingOk = Client.cancel(bookings.flatten)
        println(s"cancellingOk $cancellingOk")
      }
    }
  }

  //  test("fetch all flights of airlineA") {
  //    val url = "http://127.0.0.1:9000/airline/airlineA/flights"
  //    val call = WS.url(url).get()
  //    val result = Await.result(call, 20.seconds)
  //    println("json actual   " + result.json)
  //    val actual = Json.fromJson[Seq[FlightJson]](result.json).get
  //    println(s"actual  ${actual.size} $actual")
  //    assert(actual.size === 11)
  //  }

}