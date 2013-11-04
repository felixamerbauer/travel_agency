package models.flights

import Continent._
import org.joda.time.DateTime

object Data {
  val Airports = Seq(
    Airport("VIE", "Vienna", "Austria", Europe),
    Airport("BER", "Berlin", "Germany", Europe),
    Airport("JNB", "Johannesburg", "South Africa", Africa),
    Airport("JFK", "New York", "USA", America),
    Airport("PEK", "Beijing", "China", Asia),
    Airport("SYD", "Sydney", "Australia", Australia))

  private val baseDateTime = new DateTime(2013, 11, 1, 0, 0)

  val Flights: Seq[Flight] = {
    val src = Airports.filter(_.continent == Europe)
    val dst = Airports.filter(_.continent != Europe)
    val directions = (for {
      from <- src
      to <- dst
    } yield Seq(Direction(from, to), Direction(to, from))).flatten
    for ((a, b) <- directions.zipWithIndex) yield {
      val start = baseDateTime.plusDays(b).plusHours(b)
      Flight(b, a.from.iata, a.to.iata, start = start, end = start.plusDays(7))
    }

  }

}
