package views.formdata

import org.joda.time.format.DateTimeFormat
import models.Sex
import models.Male
import models.Female
import db._

object Commons {
  private val currencyValues = Map[Currency, Double](Euro -> 1d, Dollar -> 1.3597d, RenminbiYuan -> 8.28231883d)
  def convert(amount: Int, source: Currency, target: Currency): Int = Math.floor(currencyValues(target) / currencyValues(source) * amount).toInt

  val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")
  val uiDTF = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")
  val uiEDTF = DateTimeFormat.forPattern("EE dd.MM.yyyy HH:mm")
  val uiDF = DateTimeFormat.forPattern("dd.MM.yyyy")
  val uiTF = DateTimeFormat.forPattern("HH:mm")

  val sexesFormStringType = Map[String, Sex]("Männlich" -> Male, "Weiblich" -> Female)
  val sexesFormTypeString = sexesFormStringType map (_.swap)

  val sexes = Map("Männlich" -> false, "Weiblich" -> false)
  val sexesFirstSelected = sexes + (sexes.head._1 -> true)

  val creditCardCompanies = Map("Mastercard" -> false, "Visa" -> false)
  val creditCardCompaniesFirstSelected = creditCardCompanies + (creditCardCompanies.head._1 -> true)

  val locations = Map(Seq("Paris", "New York", "Wien", "Peking", "Sydney", "London").map(_ -> false): _*)
  val locationsFirstSelected = locations + (locations.head._1 -> true)

  val iataFullname = Map(
    "VIE" -> "Wien",
    "BER" -> "Berlin",
    "CDG" -> "Paris",
    "LHR" -> "London",
    "JFK" -> "New York",
    "PEK" -> "Peking",
    "SYD" -> "Sydney")
  val fullNameIata = iataFullname map (_.swap)

}