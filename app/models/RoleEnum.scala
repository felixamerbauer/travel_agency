package models

object RoleEnum extends Enumeration {
  type RoleEnum = Value
  val Login = Value(1, "Login")
  val Admin = Value(2, "Admin")

  private val idMap = values.map(e => (e.id, e)).toMap
  private val stringMap = values.map(e => (e.toString(), e)).toMap

  def unapply(s: String): Option[RoleEnum] = stringMap.get(s)

  def unapply(i: Int): Option[RoleEnum] = idMap.get(i)
}