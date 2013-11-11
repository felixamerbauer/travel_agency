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
        println(s"directions\n${directions.mkString("\n")}")
        Client.checkAvailability("JFK", (2013, 12, 24), (2014, 1, 15), 5, 7)
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