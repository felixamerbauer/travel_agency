package views.formdata

import scala.collection.mutable.Buffer
import play.api.data.validation.ValidationError
import models.Airline
import models.Hotelgroup
import models.Product
import models.Location
import models.Customer
import models.User

case class AdminAirlineFormData(id: String = "-1", name: String = "", apiURL: String = "") {
  def this(airline: Airline) = this(
    id = airline.id.toString,
    name = airline.name,
    apiURL = airline.apiUrl)

  def validate(): Seq[ValidationError] = {
    val errors = Buffer[ValidationError]()
    null
  }
}

case class AdminHotelgroupFormData(id: String = "-1", name: String = "", apiURL: String = "") {
  def this(hotelgroup: Hotelgroup) = this(
    id = hotelgroup.id.toString,
    name = hotelgroup.name,
    apiURL = hotelgroup.apiUrl)

  def validate(): Seq[ValidationError] = {
    val errors = Buffer[ValidationError]()
    null
  }
}

case class AdminProductFormData(id: String = "-1", from: String = "", to: String = "", archived: Boolean) {
  def this(product: Product, from: Location, to: Location) = this(
    id = product.id.toString,
    from = from.fullName,
    to = to.fullName,
    archived = product.archived)

  def validate(): Seq[ValidationError] = {
    val errors = Buffer[ValidationError]()
    null
  }
}

case class AdminCustomerFormData(
  id: String = "-1",
  firstName: String = "",
  lastName: String = "",
  email: String = "",
  password: String = "",
  birthDate: String,
  sex: List[String] = Nil,
  street: String = "",
  zipCode: String = "",
  city: String = "",
  country: String = "",
  phoneNumber: String = "",
  creditCardCompany: List[String] = Nil,
  creditCardNumber: String = "",
  creditCardExpireDate: String = "",
  creditCardVerificationCode: String = "") {

  def this(user: User,customer: Customer) = this(
    id = user.id.toString,
    firstName = customer.firstName,
    lastName = customer.lastName,
    email = user.email,
    password = "",
    birthDate = customer.birthDate.toString,
    sex = List(customer.sex),
    street = customer.street,
    zipCode = customer.zipCode,
    city = customer.city,
    country = customer.country,
    phoneNumber = customer.phoneNumber,
    creditCardCompany = List(customer.creditCardCompany),
    creditCardNumber = customer.creditCardNumber,
    creditCardExpireDate = customer.creditCardExpireDate.toString,
    creditCardVerificationCode = customer.creditCardVerificationCode)

  def validate(): Seq[ValidationError] = {
    val errors = Buffer[ValidationError]()
    null
  }
}

