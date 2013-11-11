import scala.slick.lifted.Query
import org.scalatest.BeforeAndAfter
import org.scalatest.Finders
import org.scalatest.FunSuite
import db.QueryBasics._
import Misc.db
import models.TUser
import models._
import play.api.db.slick.Config.driver.simple.Database.threadLocalSession
import play.api.db.slick.Config.driver.simple._
import play.api.test.FakeApplication
import play.api.test.Helpers.running
import org.joda.time.LocalDate
import models.ext.ExtHotel
import models.ext.TExtHotel
import models.ext.ExtFlight
import models.ext.TExtFlight
import org.joda.time.DateTime
import models.ext.ExtFlightLastModified
import models.ext.TExtFlightLastModified
import models.ext._
import scala.util.Random

class DBTests extends FunSuite with BeforeAndAfter {

  test("read write to all tables") {
    running(FakeApplication()) {
      Random.setSeed(1234)

      db.withSession {
        // TODO empty tables
        val tables = Seq(qOrder,
          qAirline,
          qHotelgroup,
          qExtHotel,
          qExtFlight,
          qProduct,
          qCustomer,
          qUser,
          qLocation,
          qCustomer,
          qUser,
          qLocation,
          qExtFlightLastModified,
          qExtHotelLastModified)
        tables foreach (_.delete)
        // user
        val users = (1 to 5 map (e => User(email = s"user$e@example.org", passwordHash = "passwordhash"))).toSeq
        println("Inserting\n\t" + users.mkString("\n\t"))
        users foreach TUser.autoInc.insert
        val usersDb = qUser.to[Seq]
        assert(users === usersDb.map(_.copy(id = -1)))
        // customer
        val customers = usersDb map { userDb =>
          Customer(userId = userDb.id, firstName = s"firstName${userDb.id}", lastName = "lastName${userDb.id}",
            birthDate = new LocalDate(2013, 12, 24), sex = "m", street = "street",
            zipCode = "1234", city = "city", country = "country",
            phoneNumber = "+43 1234567", creditCardCompany = "", creditCardNumber = "",
            creditCardExpireDate = new LocalDate(2016, 1, 1), creditCardVerificationCode = "")
        }
        println("Inserting\n\t" + customers.mkString("\n\t"))
        customers foreach TCustomer.autoInc.insert
        val customersDb = qCustomer.to[Seq]
        assert(customers === customersDb.map(_.copy(id = -1)))
        // location
        val locations = for {
          (iata, fullName) <- Seq(
            ("VIE", "Wien"),
            ("BER", "Berlin"),
            ("JNB", "Johannesburg"),
            ("JFK", "New York"),
            ("PEK", "Peking"),
            ("SYD", "Sydney"))
        } yield { Location(iataCode = iata, fullName = fullName) }
        println("Inserting\n\t" + locations.mkString("\n\t"))
        locations foreach TLocation.autoInc.insert
        val locationsDb = qLocation.to[Seq]
        assert(locations === locationsDb.map(_.copy(id = -1)))
        // product
        val products = for {
          from <- locationsDb
          to <- locationsDb
        } yield (Product(fromLocationId = from.id, toLocationId = to.id: Int, archived = false))
        println("Inserting\n\t" + products.mkString("\n\t"))
        products foreach TProduct.autoInc.insert
        val productsDb = qProduct.to[Seq]
        assert(products === productsDb.map(_.copy(id = -1)))
        // order
        val orders = for {
          customer <- customersDb.take(3)
          product <- productsDb
        } yield {
          Order(customerId = customer.id, productId = product.id, hotelName = "hotelName",
            hotelAddress = "hotelAddress", personCount = 10, roomOrderId = "1",
            toFlight = "OS 123", fromFlight = "OS 321", startDate = new LocalDate(2013, 12, 24),
            endDate = new LocalDate(2014, 1, 1), price = 149999, currency = "EUR")

        }
        println("Inserting\n\t" + orders.mkString("\n\t"))
        orders foreach TOrder.autoInc.insert
        val ordersDb = qOrder.to[Seq]
        assert(orders === ordersDb.map(_.copy(id = -1)))
        // airline
        val airlines = Seq("A", "B", "C").map(e => Airline(name = s"Airline$e", apiUrl = s"airline$e"))
        println("Inserting\n\t" + airlines.mkString("\n\t"))
        airlines foreach TAirline.autoInc.insert
        val airlinesDb = qAirline.to[Seq]
        assert(airlines === airlinesDb.map(_.copy(id = -1)))
        // hotelgroup
        val hotelGroups = Seq("X", "Y", "Z").map(e => Hotelgroup(name = s"Hotelgroup$e", apiUrl = s"hg$e"))
        println("Inserting\n\t" + hotelGroups.mkString("\n\t"))
        hotelGroups foreach THotelgroup.autoInc.insert
        val hotelGroupsDb = qHotelgroup.to[Seq]
        assert(hotelGroups === hotelGroupsDb.map(_.copy(id = -1)))
        // extHotel
        val extHotel = 0 to 30 map { idx =>
          ExtHotel(apiUrl = hotelGroupsDb(idx % hotelGroupsDb.size).apiUrl, hotelName = s"hotelName$idx", locationId = locationsDb(idx % locationsDb.size).id,
            startDate = new LocalDate(2013, 12, 24).plusDays(idx),
            endDate = new LocalDate(2013, 12, 24).plusDays(idx + 1 + idx % 14),
            personCount = 1, availableRooms = 1, price = (idx + 1) * 10, currency = "EUR")

        }
        println("Inserting\n\t" + extHotel.mkString("\n\t"))
        extHotel foreach TExtHotel.autoInc.insert
        val extHotelDb = qExtHotel.to[Seq]
        assert(extHotel === extHotelDb.map(_.copy(id = -1)))
        // extFlight
        val extFlights = 0 to 30 map { idx =>
          ExtFlight(apiUrl = airlinesDb(idx % airlinesDb.size).apiUrl, airlineName = airlinesDb(idx % airlinesDb.size).name,
            fromLocationId = locationsDb(Random.nextInt(locationsDb.size)).id,
            toLocationId = locationsDb(Random.nextInt(locationsDb.size)).id,
            dateTime = new DateTime(2013, 12, 24, 20, 15).plusDays(idx % 14),
            availableSeats = 10, price = (idx + 1) * 10, currency = "EUR")
        }
        extFlights foreach TExtFlight.autoInc.insert
        println("Inserting\n\t" + extFlights.mkString("\n\t"))
        val extFlightsDb = qExtFlight.to[Seq]
        assert(extFlights === extFlightsDb.map(_.copy(id = -1)))
        // extFlightLastModified
        val extFlightLastModified = ExtFlightLastModified(lastModified = new DateTime(2013, 11, 1, 20, 15))
        println("Inserting\n\t" + extFlightLastModified)
        TExtFlightLastModified.autoInc.insert(extFlightLastModified)
        val extFlightLastModifiedDb = Query(TExtFlightLastModified).first
        assert(extFlightLastModified === extFlightLastModifiedDb.copy(id = -1))
        // extHotelLastModified
        val extHotelLastModified = ExtHotelLastModified(lastModified = new DateTime(2013, 11, 1, 20, 15))
        println("Inserting\n\t" + extHotelLastModified)
        TExtHotelLastModified.autoInc.insert(extHotelLastModified)
        val extHotelLastModifiedDb = Query(TExtHotelLastModified).first
        assert(extHotelLastModified === extHotelLastModifiedDb.copy(id = -1))
      }
    }

  }
}