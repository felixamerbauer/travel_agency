package views.formdata

import scala.collection.mutable.Buffer
import play.api.data.validation._
import models.Airline
import models.Hotelgroup
import models.Product
import models.Location
import models.Customer
import models.User
import views.formdata.Commons.dateFormat
import play.api.data.validation.Constraint


case class RegistrationFormData(
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
  creditCardVerificationCode: String = "") 

object RegistrationFormData {
  val constraints: Constraint[RegistrationFormData] = Constraint("constraints")({ data =>
    val errors = Buffer[ValidationError]()
    Valid
  })
}


