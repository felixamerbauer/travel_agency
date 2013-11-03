import models.flights.Airport
import models.flights.Airports.Airports
import play.api.mvc.PathBindable

package object binders {
  implicit def airportPathBindable(implicit stringBinder: PathBindable[String]) = new PathBindable[Airport] {

    def bind(key: String, value: String): Either[String, Airport] =
      for {
        iata <- stringBinder.bind(key, value).right
        article <- Airports.find(_.iata == iata).toRight("Airport not found").right
      } yield article

    def unbind(key: String, airport: Airport): String =
      stringBinder.unbind(key, airport.iata)

  }
}