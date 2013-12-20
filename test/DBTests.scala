import scala.slick.lifted.Query
import org.scalatest.BeforeAndAfter
import org.scalatest.Finders
import org.scalatest.FunSuite
import db.QueryBasics._
import db.Currency
import Misc.mydb
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
import scala.util.Random._
import org.joda.time.DateMidnight

class DBTests extends FunSuite with BeforeAndAfter {
  def randomDescription = (1 to 10).map(_ => alphanumeric.take(nextInt(10) + 2).mkString).mkString(" ")

  test("read write to all tables") {
    running(FakeApplication()) {
      // initialize random seed so we get consitent results
      setSeed(1234)

      mydb.withSession {
        // TODO empty tables
        val tables = Seq(qOrder,
          qAirline,
          qExtFlightBooking,
          qExtHotelBooking,
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
        val users = (1 to 5 map (e => User(email = s"user$e@example.org", passwordHash = "pwd"))).toSeq
        println("Inserting\n\t" + users.mkString("\n\t"))
        users foreach TUser.autoInc.insert
        val usersDb = qUser.to[Seq]
        assert(users === usersDb.map(_.copy(id = -1)))
        // customer
        val customers = usersDb map { userDb =>
          Customer(userId = userDb.id, firstName = s"firstName${userDb.id}", lastName = s"lastName${userDb.id}",
            birthDate = new DateMidnight(2013, 12, 24), sex = Seq(Male, Female)(nextInt(2)), street = "street",
            zipCode = "1234", city = "city", country = "country",
            phoneNumber = "+43 1234567", creditCardCompany = Seq("MasterCard", "Visa")(nextInt(2)), creditCardNumber = "1234432112344321",
            creditCardExpireDate = new DateMidnight(2016, 1, 1), creditCardVerificationCode = "123")
        }
        println("Inserting\n\t" + customers.mkString("\n\t"))
        customers foreach TCustomer.autoInc.insert
        val customersDb = qCustomer.to[Seq]
        assert(customers === customersDb.map(_.copy(id = -1)))
        // location
        val startLocations = for {
          (iata, fullName) <- Seq(
            ("VIE", "Wien"),
            //            ("BER", "Berlin"),
            ("CDG", "Paris"),
            ("LHR", "Heathrow"))
        } yield { Location(iataCode = iata, fullName = fullName, start = true) }
        val endLocations = for {
          (iata, fullName) <- Seq(
            //            ("JNB", "Johannesburg"),
            ("JFK", "New York"),
            ("PEK", "Peking"),
            ("SYD", "Sydney"))
        } yield { Location(iataCode = iata, fullName = fullName, start = false) }
        println("Inserting start locations\n\t" + startLocations.mkString("\n\t"))
        startLocations foreach TLocation.autoInc.insert
        println("Inserting end locations\n\t" + endLocations.mkString("\n\t"))
        endLocations foreach TLocation.autoInc.insert
        val allLocationsDb = qLocation.to[Seq]
        val startLocationsDb = allLocationsDb.filter(l => startLocations.exists(_.iataCode == l.iataCode))
        val endLocationsDb = allLocationsDb.filter(l => endLocations.exists(_.iataCode == l.iataCode))
        assert((startLocations) === startLocationsDb.map(_.copy(id = -1)))
        assert((endLocations) === endLocationsDb.map(_.copy(id = -1)))
        // product
        val products = for {
          from <- startLocationsDb
          to <- endLocationsDb
        } yield (Product(fromLocationId = from.id, toLocationId = to.id: Int, archived = false))
        println("Inserting\n\t" + products.mkString("\n\t"))
        products foreach TProduct.autoInc.insert
        val productsDb = qProduct.to[Seq]
        assert(products === productsDb.map(_.copy(id = -1)))
        // order
        //        val orders = for {
        //          customer <- customersDb.take(3)
        //          product <- productsDb
        //        } yield {
        //          Order(customerId = customer.id, productId = product.id, hotelName = "hotelName",
        //            hotelAddress = "hotelAddress", personCount = 10, roomOrderId = "1",
        //            toFlight = "OS 123", fromFlight = "OS 321", startDate = new DateMidnight(2014, 2, 3),
        //            endDate = new DateMidnight(2014, 2, 16), price = 149999, currency = "EUR")
        //
        //        }
        //        println("Inserting\n\t" + orders.mkString("\n\t"))
        //        orders foreach TOrder.autoInc.insert
        //        val ordersDb = qOrder.to[Seq]
        //        assert(orders === ordersDb.map(_.copy(id = -1)))
        // airline
        val airlines = Seq(("Austrian", "OS"), ("Lufthansa", "LH"), ("Swiss", "LX")).map(e => Airline(name = e._1, apiUrl = e._2))
        println("Inserting\n\t" + airlines.mkString("\n\t"))
        airlines foreach TAirline.autoInc.insert
        val airlinesDb = qAirline.to[Seq]
        assert(airlines === airlinesDb.map(_.copy(id = -1)))
        // hotelgroup
        val hotelGroups = Seq(("InterContintental","ic"), ("Marriot","ma"), ("Hilton","hi")).map(e => Hotelgroup(name = e._1, apiUrl = e._2))
        println("Inserting\n\t" + hotelGroups.mkString("\n\t"))
        hotelGroups foreach THotelgroup.autoInc.insert
        val hotelGroupsDb = qHotelgroup.to[Seq]
        assert(hotelGroups === hotelGroupsDb.map(_.copy(id = -1)))
        // extHotel
        val extHotel = for {
          startDay <- 0 until 14
          endDay <- (startDay + 1) to 14
          locationIdx <- 0 until endLocationsDb.size
          hotelGroupsIdx <- 0 until hotelGroupsDb.size
        } yield {
          ExtHotel(apiUrl = hotelGroupsDb(hotelGroupsIdx).apiUrl, hotelName = s"hotelName${nextInt(100)}", description = randomDescription, category = nextInt(5) + 1, locationId = endLocationsDb(locationIdx).id,
            startDate = new DateMidnight(2014, 2, 3).plusDays(startDay),
            endDate = new DateMidnight(2014, 2, 3).plusDays(endDay),
            availableRooms = 100, price = (nextInt(91) + 10) * (endDay - startDay), currency = Currencies(nextInt(Currencies.size)))
        }
        println("Inserting\n\t" + extHotel.mkString("\n\t"))
        extHotel foreach TExtHotel.autoInc.insert
        val extHotelDb = qExtHotel.to[Seq]
        println("Read\n\t" + extHotelDb.map(_.copy(id = -1)).mkString("\n\t"))
        println("result: " + (extHotel === extHotelDb.map(_.copy(id = -1))))
        assert(extHotel === extHotelDb.map(_.copy(id = -1)))
        // extFlight - outward
        val extFlightsOutward = for {
          day <- 0 to 7
          airlineIdx <- 0 until airlinesDb.size
          startLocationIdx <- 0 until startLocationsDb.size
          endLocationIdx <- 0 until endLocationsDb.size
        } yield {
          ExtFlight(apiUrl = airlinesDb(airlineIdx).apiUrl, airlineName = airlinesDb(airlineIdx).name,
            fromLocationId = startLocationsDb(startLocationIdx).id,
            toLocationId = endLocationsDb(endLocationIdx).id,
            dateTime = new DateTime(2014, 2, 3, nextInt(18) + 6, nextInt(12) * 5).plusDays(day % 14),
            availableSeats = 100, price = nextInt(991) + 10, currency = Currencies(nextInt(Currencies.size)))
        }
        val extFlightsInward = for {
          day <- 0 to 7
          airlineIdx <- 0 until airlinesDb.size
          startLocationIdx <- 0 until startLocationsDb.size
          endLocationIdx <- 0 until endLocationsDb.size
        } yield {
          ExtFlight(apiUrl = airlinesDb(airlineIdx).apiUrl, airlineName = airlinesDb(airlineIdx).name,
            fromLocationId = endLocationsDb(endLocationIdx).id,
            toLocationId = startLocationsDb(startLocationIdx).id,
            dateTime = new DateTime(2014, 2, 3, nextInt(18) + 6, nextInt(12) * 5).plusDays(day),
            availableSeats = 10, price = nextInt(991) + 10, currency = Currencies(nextInt(Currencies.size)))
        }
        println("Inserting outward\n\t" + extFlightsOutward.mkString("\n\t"))
        extFlightsOutward foreach TExtFlight.autoInc.insert
        println("Inserting inward\n\t" + extFlightsInward.mkString("\n\t"))
        extFlightsInward foreach TExtFlight.autoInc.insert
        val extFlightsDb = qExtFlight.to[Seq]
        assert((extFlightsOutward ++ extFlightsInward) === extFlightsDb.map(_.copy(id = -1)))
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
        // extFlightBooking
        val extFlightBooking = ExtFlightBooking(extFlightId = extFlightsDb.head.id, seats = 1)
        println(s"Inserting $extFlightBooking")
        TExtFlightBooking.insert(extFlightBooking)
        val extFlightBookingDb = qExtFlightBooking.to[Seq].head
        assert(extFlightBooking === extFlightBooking.copy(id = -1))
        qExtFlightBooking.delete
        // extFlightBooking
        val extHotelBooking = ExtHotelBooking(extHotelId = extHotelDb.head.id, rooms = 1)
        println(s"Inserting $extHotelBooking")
        TExtHotelBooking.insert(extHotelBooking)
        val extHotelBookingDb = qExtHotelBooking.to[Seq].head
        assert(extHotelBooking === extHotelBookingDb.copy(id = -1))
        qExtHotelBooking.delete

      }
    }

  }
}