package views.formdata

import scala.collection.mutable.Buffer

import play.api.data.validation.ValidationError
import play.api.Logger.info

case class LoginFormData(email: String = "", password: String = "") {

  def validate(): Seq[ValidationError] = {
    info("validate")
    val errors = Buffer[ValidationError]()
    null
  }
}
