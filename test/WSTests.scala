

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import controllers.JsonDeSerialization.flightReads
import models.flights.Data
import models.json.flights.FlightJson
import play.api.libs.json.Json
import play.api.libs.ws.WS
import org.scalatest.Finders

class WSTests extends FunSuite with BeforeAndAfter {
  implicit val context = ExecutionContext.Implicits.global

  test("fetch single flight of Austrian") {
    val url = "http://127.0.0.1:9000/airline/austrian/flights/1"
    val call = WS.url(url).get()
    val result = Await.result(call, 20.seconds)
    println("json actual   " + result.json)
    val actual = Json.fromJson[FlightJson](result.json).get
    val expected = Data.Flights.find(_.id == 1).map(new FlightJson(_)).get
    println("actual  " + actual)
    println("expectd " + expected)
    assert(actual === expected)
  }

  test("fetch all flights of Austrian") {
    val url = "http://127.0.0.1:9000/airline/austrian/flights"
    val call = WS.url(url).get()
    val result = Await.result(call, 20.seconds)
    println("json actual   " + result.json)
    val actual = Json.fromJson[Seq[FlightJson]](result.json).get
    val expected = Data.Flights.map(new FlightJson(_))
    println(s"actual  ${actual.size} $actual")
    println(s"expectd ${expected.size} $actual")
    assert(actual === expected)
  }

}