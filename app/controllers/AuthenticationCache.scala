package controllers

import models.Person
import models.QueryMethods.findAllUsersWithRoles
import models.RoleEnum.RoleEnum
import play.api.Play.current
import play.api.db.slick.DB
import play.api.db.slick.Session
import play.api.Logger._

// TODO not thread safe
object AuthenticationCache {

  private def fillCache = DB.withSession { implicit s: Session =>
    val data = findAllUsersWithRoles
    val seq = data.map(e => (e._1.authToken.get, e))
    Map(seq: _*)
  }

  private var cache: Map[String, Tuple2[Person, Set[RoleEnum]]] = {
    debug("initializing authentication cache")
    fillCache
  }

  def update {
    debug("updating authentication cache")
    cache = fillCache
  }

  def find(authToken: String): Option[Tuple2[Person, Set[RoleEnum]]] = cache.get(authToken)

}