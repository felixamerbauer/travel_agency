package views.formdata

import scala.collection.mutable.Buffer

import play.api.data.validation.ValidationError

case class SearchFormData(
  from: List[String] = Nil,
  to: List[String] = Nil,
  start: String = "",
  end: String = "",
  adults: List[String] = Nil,
  children: List[String] = Nil,
  category: List[String] =  Nil) {

  //  def this(name: String, password: String, level: GradeLevel, gpa: GradePointAverage, hobbies: Seq[Hobby], majors: Seq[Major]) = this(
  //    name = name,
  //    password = password,
  //    level = level.name,
  //    gpa = gpa.name,
  //    hobbies = hobbies.map(_.name).toList,
  //    majors = majors.map(_.name).toList)

  def validate(): Seq[ValidationError] = {
    val errors = Buffer[ValidationError]()
    //    if (name == null || name.length == 0) {
    //      errors += new ValidationError("name", "No name was given.")
    //    }
    //    if (password == null || password.length == 0) {
    //      errors += new ValidationError("password", "No password was given.")
    //    } else if (password.length < 5) {
    //      errors += new ValidationError("password", "Given password is less than five characters.")
    //    }
    //    if (hobbies.size > 0) {
    //      for (hobby <- hobbies if Hobby.findHobby(hobby) == null) {
    //        errors += new ValidationError("hobbies", "Unknown hobby: " + hobby + ".")
    //      }
    //    }
    //    if (level == null || level.length == 0) {
    //      errors += new ValidationError("level", "No grade level was given.")
    //    } else if (GradeLevel.findLevel(level) == null) {
    //      errors += new ValidationError("level", "Invalid grade level: " + level + ".")
    //    }
    //    if (gpa == null || gpa.length == 0) {
    //      errors += new ValidationError("gpa", "No gpa was given.")
    //    } else if (GradePointAverage.findGPA(gpa) == null) {
    //      errors += new ValidationError("gpa", "Invalid GPA: " + gpa + ".")
    //    }
    //    if (majors.size > 0) {
    //      for (major <- majors if Major.findMajor(major) == null) {
    //        errors += new ValidationError("majors", "Unknown Major: " + major + ".")
    //      }
    //    }
    //    if (errors.size > 0) return errors
    null
  }
}
