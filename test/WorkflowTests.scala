import scala.concurrent.ExecutionContext

import org.joda.time.DateMidnight
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite

import Misc.mydb
import controllers.Client
import db.QueryBasics.qCustomer
import play.api.db.slick.Config.driver.simple.Database.threadLocalSession
import play.api.db.slick.Config.driver.simple.queryToQueryInvoker
import play.api.test.FakeApplication
import play.api.test.Helpers.running

class WorkflowTests extends FunSuite with BeforeAndAfter {
  implicit val context = ExecutionContext.Implicits.global

  implicit def tuple3ToDateMidnight(t: Tuple3[Int, Int, Int]): DateMidnight = {
    val (year, month, day) = t
    new DateMidnight(year, month, day)
  }

  test("complete workflow") {
    running(FakeApplication()) {

      mydb.withSession {
        val directions = Client.fetchDirections
        println(s"directions\n\t${directions.mkString("\n\t")}")
        // choose first
        val chosenDirection = directions.head
        println(s"chosen direction $chosenDirection")

        val journeys = Client.checkAvailability(chosenDirection.from, chosenDirection.to, (2014, 2, 3), (2014, 2, 5), adults = 2, children = 2)
        println(s"packages\n\t${journeys.map(_.pretty).mkString("\n\t")}")
        // choose cheapest
        val chosenJourney = journeys.minBy(_.price)
        println(s"chosen journey ${chosenJourney.pretty}")
        val customer = qCustomer.list.head
        Client.book(chosenJourney, customer)
      }
    }
  }
}