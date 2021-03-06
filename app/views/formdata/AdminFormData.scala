package views.formdata

import scala.collection.mutable.Buffer
import play.api.data.validation._
import models.Airline
import models.Hotelgroup
import models.Product
import models.Location
import models.Customer
import models.User
import views.formdata.Commons._
import play.api.data.validation.Constraint

case class AdminAirlineFormData(id: Int = -1, name: String = "", apiURL: String = "") {
  def this(airline: Airline) = this(
    id = airline.id,
    name = airline.name,
    apiURL = airline.apiUrl)
}

object AdminAirlineFormData {
  val constraints: Constraint[AdminAirlineFormData] = Constraint("constraints")({ data =>
    val errors = Buffer[ValidationError]()
    if (data.name.length() < 3) errors += ValidationError("name", "Name ist zu kurz")
    if (data.name.length() > 20) errors += ValidationError("name", "Name ist zu lang")
    if (errors.isEmpty) Valid else Invalid(errors.toSeq)
  })
}

case class AdminHotelgroupFormData(id: Int = -1, name: String = "", apiURL: String = "") {
  def this(hotelgroup: Hotelgroup) = this(
    id = hotelgroup.id,
    name = hotelgroup.name,
    apiURL = hotelgroup.apiUrl)
}

object AdminHotelgroupFormData

case class AdminProductFormData(id: Int = -1, from: String = "", to: String = "", archived: Boolean) {
  def this(product: Product, from: Location, to: Location) = this(
    id = product.id,
    from = from.fullName,
    to = to.fullName,
    archived = product.archived)
}

object AdminProductFormData {
  def constraints: Constraint[AdminProductFormData] = Constraint("constraints")({ data =>
    val errors = Buffer[ValidationError]()
    if (data.from == data.to)
      errors += ValidationError("from", "Von und nach müssen sich unterscheiden")
    if (errors.isEmpty) Valid else Invalid(errors.toSeq)
  })
}

case class AdminCustomerFormData(
  id: Int = -1,
  userId: Int = -1,
  firstName: String = "",
  lastName: String = "",
  email: String = "",
  password: String = "",
  birthDate: String,
  sex: String = "",
  street: String = "",
  zipCode: String = "",
  city: String = "",
  country: String = "",
  phoneNumber: String = "",
  creditCardCompany: String = "",
  creditCardNumber: String = "",
  creditCardExpireDate: String = "",
  creditCardVerificationCode: String = "") {

  def this(user: User, customer: Customer) = this(
    id = customer.id,
    userId = user.id,
    firstName = customer.firstName,
    lastName = customer.lastName,
    email = user.email,
    password = "",
    birthDate = dateFormat.print(customer.birthDate),
    sex = sexesFormTypeString(customer.sex),
    street = customer.street,
    zipCode = customer.zipCode,
    city = customer.city,
    country = customer.country,
    phoneNumber = customer.phoneNumber,
    creditCardCompany = customer.creditCardCompany,
    creditCardNumber = customer.creditCardNumber,
    creditCardExpireDate = dateFormat.print(customer.creditCardExpireDate),
    creditCardVerificationCode = customer.creditCardVerificationCode)
}

