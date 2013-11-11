import play.api.db.slick.Config.driver.simple._
import db.QueryLibrary._
import play.api.test._
import play.api.test.Helpers._
import Misc.db
import models.TUser
import models.TCustomer
import models.TProduct
import models.TLocation
import models.TOrder
import models.TAirline
import models.THotelgroup
import models.ext._
import models.ext.TExtFlight
import models.ext.TExtFlightLastModified

object PrintDDL extends App {
  import play.api.db.slick.Config.driver.simple.Database.threadLocalSession

  running(FakeApplication()) {

    db.withSession {
      def print(it: Iterator[String]) = println(it.mkString("", ";\n", ";"))
      print(TUser.ddl.createStatements)
      print(TCustomer.ddl.createStatements)
      print(TLocation.ddl.createStatements)
      print(TProduct.ddl.createStatements)
      print(TOrder.ddl.createStatements)
      print(TAirline.ddl.createStatements)
      print(THotelgroup.ddl.createStatements)
      print(TExtHotel.ddl.createStatements)
      print(TExtFlight.ddl.createStatements)
      print(TExtFlightLastModified.ddl.createStatements)
      print(TExtHotelLastModified.ddl.createStatements)
    }
  }
}

object TestQuery extends App {
  import play.api.db.slick.Config.driver.simple.Database.threadLocalSession

  running(FakeApplication()) {
    println(qFlightsWithLocation("airlineShortName", 1)._selectStatement)
  }

}