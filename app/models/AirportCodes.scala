package models

object Continent extends Enumeration {
  type Continent = Value
  val Africa, America, Asia, Australia, Europe = Value
}

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

case class Airport(iata: String, city: String, country: String, continent: Continent)