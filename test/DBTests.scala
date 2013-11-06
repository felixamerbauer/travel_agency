import scala.slick.lifted.Query

import org.scalatest.BeforeAndAfter
import org.scalatest.Finders
import org.scalatest.FunSuite

import Misc.db
import models.TUser
import models.User
import play.api.db.slick.Config.driver.simple.Database.threadLocalSession
import play.api.db.slick.Config.driver.simple._
import play.api.test.FakeApplication
import play.api.test.Helpers.running

class DBTests extends FunSuite with BeforeAndAfter {

  test("read write to all tables") {
    running(FakeApplication()) {
      db.withSession {
        val user = User(email = "user@example.org", passwordHash = "passwordhash")
        TUser.autoInc.insert(user)
        val userDb = Query(TUser).where(_.email === user.email).to[Vector].head
        assert(user === userDb.copy(id = -1))
      }
    }

  }
}