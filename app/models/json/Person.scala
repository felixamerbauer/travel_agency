package models.json

import models.PersonId

case class PersonOverview(
  id: PersonId,
  firstname: String,
  lastname: String)