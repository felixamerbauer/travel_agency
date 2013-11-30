package views.formdata

import org.joda.time.format.DateTimeFormat

object Commons {
  val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

  val sexes = Map("Männlich" -> false, "Weiblich" -> false)
  val sexesFirstSelected = sexes + (sexes.head._1 -> true)

  val creditCardCompanies = Map("Mastercard" -> false, "Visa" -> false)
  val creditCardCompaniesFirstSelected = creditCardCompanies + (creditCardCompanies.head._1 -> true)

  val locations = Map(Seq("Paris", "New York", "Wien", "Peking", "Sydney", "London").map(_ -> false): _*)
  val locationsFirstSelected = locations + (locations.head._1 -> true)
}