package models

import org.joda.time.DateTime

class Misc {
  case class Direction(from: String, to: String)

  case class Window(start: DateTime, end: DateTime)

}