import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import controllers.JsonDeSerialization._
import json._
import play.api.libs.json.Json
import play.api.libs.ws.WS
import org.scalatest.Finders
import models.Direction

class WSTests extends FunSuite with BeforeAndAfter {
  implicit val context = ExecutionContext.Implicits.global

  test("fetch directions of airlineA") {
    val url = "http://127.0.0.1:9000/airline/airlineA/directions"
    val call = WS.url(url).get()
    val result = Await.result(call, 20.seconds)
    println("json actual   " + result.json)
    val actual = Json.fromJson[Seq[Direction]](result.json).get
    println("actual  " + actual)
    assert(actual.size === 11)
  }

  test("fetch all flights of airlineA") {
    val url = "http://127.0.0.1:9000/airline/airlineA/flights"
    val call = WS.url(url).get()
    val result = Await.result(call, 20.seconds)
    println("json actual   " + result.json)
    val actual = Json.fromJson[Seq[FlightJson]](result.json).get
    println(s"actual  ${actual.size} $actual")
    assert(actual.size === 11)
  }

  test("fetch all vienna flights of airlineA") {
    val url = "http://127.0.0.1:9000/airline/airlineA/flights?from=VIE"
    val call = WS.url(url).get()
    val result = Await.result(call, 20.seconds)
    println("json actual   " + result.json)
    val actual = Json.fromJson[Seq[FlightJson]](result.json).get
    println(s"actual  ${actual.size} $actual")
    assert(actual.size === 1)
  }

}