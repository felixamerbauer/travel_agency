package db
import play.api.db.slick.Config.driver.simple._
import db.QueryLibrary._
import models.RoleEnum.RoleEnum
import db.QueryBasics._
import com.github.t3hnar.bcrypt.BCrypt
import com.github.t3hnar.bcrypt.Password
import play.api.Logger._
import models._

object QueryMethods {
  val defaultPassword = "password".bcrypt(BCrypt.gensalt())

//  // Complex Queries
//  def createEvent(event: Event, memberships: Seq[MembershipEnum], starts: Seq[DateTime])(implicit session: Session): EventId = {
//    info(s"createEvent $event ${memberships.mkString(", ")} ${starts.mkString(", ")}")
//    session.withTransaction {
//      val eventId = TEvent.autoInc.insert(event)
//      memberships.foreach { membership =>
//        TEventMembership.autoInc.insert(EventMembership(eventId = eventId, membership = membership))
//      }
//      starts.foreach { start =>
//        TEventStart.autoInc.insert(EventStart(eventId = eventId, start = start))
//      }
//      eventId
//    }
//  }
//
//  def deleteEvent(eid: EventId)(implicit session: Session): Unit = {
//    info("deleteEvent " + eid)
//    session.withTransaction {
//      debug("Deleting Memberships")
//      qEventMemberships.where(_.eventId === eid).delete
//      debug("Deleting Starts")
//      qEventStarts.where(_.eventId === eid).delete
//      debug("Deleting Event")
//      qEvents.where(_.id === eid).delete
//    }
//  }
//
//  def updateEvent(event: Event, memberships: Seq[MembershipEnum], starts: Seq[DateTime])(implicit session: Session): Unit = {
//    def updateEvent(id: EventId) = qEvents.where(_.id === id).map(
//      r => r.title ~ r.description ~ r.location ~ r.typ ~ r.season)
//    info(s"Updating $event / ${memberships.mkString(", ")} / ${starts.mkString(", ")}")
//    session.withTransaction {
//      updateEvent(event.id).update(event.title, event.description, event.location, event.typ, event.season)
//      // TODO more efficient save only changes
//      qEventMemberships.where(_.eventId === event.id).delete
//      memberships.foreach { membership =>
//        TEventMembership.autoInc.insert(EventMembership(eventId = event.id, membership = membership))
//      }
//      // TODO more efficient save only changes
//      qEventStarts.where(_.eventId === event.id).delete
//      starts.foreach { start =>
//        TEventStart.autoInc.insert(EventStart(eventId = event.id, start = start))
//      }
//    }
//  }

//  def findAllUsersWithRoles(implicit session: Session): Seq[(Person, Set[RoleEnum])] = ??? 
//  {
//    // ToDOfilter not null authToken
//    val data = personRolesJoin.to[Vector]
//    val dataFiltered = data.filter(_._1.authToken.isDefined)
//    (for ((person, personPersonRoles) <- dataFiltered.groupBy(_._1)) yield {
//      (person, personPersonRoles.map(_._2).toSet)
//    }).toSeq
//  }
  
//  def findByAuthToken(authToken: String)(implicit session: Session): Option[(Person, Set[RoleEnum])] = ???
//    qPersons.where(_.authToken === authToken).firstOption.map { e =>
//      val roles = qPersonRoles.where(f => f.personId === e.id).map(_.role).to[Set]
//      (e, roles)
//    }

  val lower = SimpleFunction.unary[String, String]("LOWER")

//  def findByUsernamePassword(username: String, password: String)(implicit session: Session): Option[(Person, Set[RoleEnum])] = ??? 
//  {
//    username.split("""\.""") match {
//      case Array(firstname, lastname) =>
//        qPersons.where(e => lower(e.firstname) === firstname && lower(e.lastname) === lastname).firstOption match {
//          case Some(person) =>
//            if (password.isBcrypted(person.password)) {
//              debug(s"found person ${person.id} for username $username")
//              val roles = qPersonRoles.where(e => e.personId === person.id).map(_.role).to[Set]
//              Some(person, roles)
//            } else {
//              debug(s"wrong password for person ${person.id} for username $username")
//              None
//            }
//          case _ =>
//            None
//        }
//      case _ =>
//        debug(s"Invalid username $username")
//        None
//    }
//  }

//  def updateToken(person: Person, token: String)(implicit session: Session) {
//    info(s"updateToken $person $token")
//    qPersons.where(_.id === person.id).map(_.authToken).update(Some(token))
//    AuthenticationCache.update
//  }
//
//  def deleteAuthToken(person: Person)(implicit session: Session) {
//    info(s"deleteAuthToken $person")
//    qPersons.where(_.id === person.id).map(_.authToken).update(None)
//    AuthenticationCache.update
//  }

}