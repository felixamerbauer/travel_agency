import scala.slick.lifted.Query
import org.scalatest.BeforeAndAfter
import org.scalatest.Finders
import org.scalatest.FunSuite
import Misc.db
import models.TUser
import models._
import play.api.db.slick.Config.driver.simple.Database.threadLocalSession
import play.api.db.slick.Config.driver.simple._
import play.api.test.FakeApplication
import play.api.test.Helpers.running
import org.joda.time.LocalDate
import models.ext.ExtHotelRoom
import models.ext.TExtHotelRoom
import models.ext.ExtFlight
import models.ext.TExtFlight
import org.joda.time.DateTime
import models.ext.ExtFlightLastModified
import models.ext.TExtFlightLastModified
import models.ext.ExtHotelRoomLastModified
import models.ext.TExtHotelRoomLastModified

class DBTests extends FunSuite with BeforeAndAfter {

  test("read write to all tables") {
    running(FakeApplication()) {

      db.withSession {
        // TODO empty tables
        //        Query(TUser).delete
        // user
        val user = User(email = "user@example.org", passwordHash = "passwordhash")
        TUser.autoInc.insert(user)
        val userDb = Query(TUser).where(_.email === user.email).first
        assert(user === userDb.copy(id = -1))
        // customer
        val customer = Customer(userId = userDb.id, firstName = "firstName", lastName = "lastName",
          birthDate = new LocalDate(2013, 12, 24), sex = "m", street = "street",
          zipCode = "1234", city = "city", country = "country",
          phoneNumber = "+43 1234567", creditCardCompany = "", creditCardNumber = "",
          creditCardExpireDate = "", creditCardVerificationCode = "")
        TCustomer.autoInc.insert(customer)
        val customerDb = Query(TCustomer).where(_.firstName === "firstName").first
        assert(customer === customerDb.copy(id = -1))
        // location
        val location1 = Location(iataCode = "BER", fullName = "Berlin")
        val location2 = Location(iataCode = "JFK", fullName = "New York")
        TLocation.autoInc.insert(location1)
        TLocation.autoInc.insert(location2)
        val location1Db = Query(TLocation).where(_.iataCode === "BER").first
        val location2Db = Query(TLocation).where(_.iataCode === "JFK").first
        assert(location1 === location1Db.copy(id = -1))
        assert(location2 === location2Db.copy(id = -1))
        // product
        val product = Product(fromLocationId = location1Db.id, toLocationId = location2Db.id: Int, archived = false)
        TProduct.autoInc.insert(product)
        val productDb = Query(TProduct).where(_.fromLocationId === location1Db.id).first
        assert(product === productDb.copy(id = -1))
        // order
        val order = Order(customerId = customerDb.id, productId = productDb.id, hotelName = "hotelName",
          hotelAddress = "hotelAddress", personCount = 10, roomOrderId = "1",
          toFlight = "OS 123", fromFlight = "OS 321", startDate = new LocalDate(2013, 12, 24),
          endDate = new LocalDate(2014, 1, 1), price = 1499.99, currency = "EUR")
        TOrder.autoInc.insert(order)
        val orderDb = Query(TOrder).where(_.hotelName === "hotelName").first
        assert(order === orderDb.copy(id = -1))
        // airline
        val airline = Airline(name = "Austrian", apiUrl = "austrian")
        TAirline.autoInc.insert(airline)
        val airlineDb = Query(TAirline).where(_.name === "Austrian").first
        assert(airline === airlineDb.copy(id = -1))
        // hotelgroup
        val hotelGroup = HotelGroup(name = "Hilton", apiUrl = "hilton")
        THotelGroup.autoInc.insert(hotelGroup)
        val hotelGroupDb = Query(THotelGroup).where(_.name === "Hilton").first
        assert(hotelGroup === hotelGroupDb.copy(id = -1))
        // extHotelRoom
        val extHotelRoom = ExtHotelRoom(hotelShortName = "hotelShortName", hotelName = "hotelName", locationId = location1Db.id,
          startDate = new LocalDate(2013, 12, 24), endDate = new LocalDate(2013, 12, 24), personCount = 1, availableRooms = 1)
        TExtHotelRoom.autoInc.insert(extHotelRoom)
        val extHotelRoomDb = Query(TExtHotelRoom).where(_.hotelShortName === "hotelShortName").first
        assert(extHotelRoom === extHotelRoomDb.copy(id = -1))
        // extFlight
        val extFlight = ExtFlight(airlineShortName = "airlineShortName", airlineName = "airlineName", fromLocationId = location1Db.id,
          toLocationId = location2Db.id, dateTime = new DateTime(2013, 12, 24, 20, 15), availableSeats = 1, price = 199.90)
        TExtFlight.autoInc.insert(extFlight)
        val extFlightDb = Query(TExtFlight).where(_.airlineShortName === "airlineShortName").first
        assert(extFlight === extFlightDb.copy(id = -1))
        // extFlightLastModified
        val extFlightLastModified = ExtFlightLastModified(lastModified = new DateTime(2013, 12, 24, 20, 15))
        TExtFlightLastModified.autoInc.insert(extFlightLastModified)
        val extFlightLastModifiedDb = Query(TExtFlightLastModified).first
        assert(extFlightLastModified === extFlightLastModifiedDb.copy(id = -1))
        // extHotelRoomLastModified
        val extHotelRoomLastModified = ExtHotelRoomLastModified(lastModified = new DateTime(2013, 12, 24, 20, 15))
        TExtHotelRoomLastModified.autoInc.insert(extHotelRoomLastModified)
        val extHotelRoomLastModifiedDb = Query(TExtHotelRoomLastModified).first
        assert(extHotelRoomLastModified === extHotelRoomLastModifiedDb.copy(id = -1))
      }
    }

  }
}