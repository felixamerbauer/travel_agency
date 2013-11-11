import scala.concurrent.ExecutionContext

import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite

import Misc.db
import controllers.Client
import play.api.db.slick.Config.driver.simple.Database.threadLocalSession
import play.api.test.FakeApplication
import play.api.test.Helpers.running

class WorkflowTests extends FunSuite with BeforeAndAfter {
  implicit val context = ExecutionContext.Implicits.global

  test("complete workflow") {
    running(FakeApplication()) {

      db.withSession {
        val directions = Client.fetchDirections
        println(s"directions\n${directions.mkString("\n")}")
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