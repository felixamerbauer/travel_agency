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
import models.THotelGroup
import models.ext.TExtHotelRoom
import models.ext.TExtFlight

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
      print(THotelGroup.ddl.createStatements)
      print(TExtHotelRoom.ddl.createStatements)
      print(TExtFlight.ddl.createStatements)
    }
  }
}