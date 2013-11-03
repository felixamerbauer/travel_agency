package models.flights

import Continent._

object Airports {
  val Airports = Seq(
    Airport("VIE", "Vienna", "Austria", Europe),
    Airport("BER", "Berlin", "Germany", Europe),
    Airport("JNB", "Johannesburg", "South Africa", Africa),
    Airport("JFK", "New York", "USA", America),
    Airport("PEK", "Beijing", "China", Asia),
    Airport("SYD", "Sydney", "Australia", Australia))
}
