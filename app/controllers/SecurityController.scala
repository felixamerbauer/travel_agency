//package controllers
//
//import java.util.UUID
//import java.util.concurrent.TimeUnit
//import scala.annotation.implicitNotFound
//import controllers.JsonDeSerialization.loginReads
//import models.Person
//import models.QueryMethods.deleteAuthToken
//import models.QueryMethods.findByAuthToken
//import models.QueryMethods.findByUsernamePassword
//import models.QueryMethods.updateToken
//import models.RoleEnum
//import models.RoleEnum.RoleEnum
//import models.json.Login
//import play.api.Play.current
//import play.api.db.slick.DBAction
//import play.api.db.slick.DBSessionRequest
//import play.api.db.slick.dbSessionRequestAsSession
//import play.api.libs.json.Json
//import play.api.libs.json.Json.toJsFieldJsValueWrapper
//import play.api.mvc.Controller
//import play.api.mvc.Cookie
//import play.api.mvc.DiscardingCookie
//import play.api.mvc.Result
//import play.api.mvc.WrappedRequest
//import play.api.mvc.SimpleResult
//import play.api.Logger._
//
//case class AuthenticatedRequest(user: Person, rs: DBSessionRequest) extends WrappedRequest(rs.request)
//
//object SecurityController extends Controller with CtrlHelper {
//  // couple of common roles
//  val rolesLogin = Set(RoleEnum.Login)
//  val rolesAdmin = Set(RoleEnum.Admin)
//
//  private val AuthTokenHeader = "X-AUTH-TOKEN";
//  private val AuthToken = "authToken";
//  private val Admin = "admin";
//
//  private def isAuthenticated(header: Seq[String], roles: Set[RoleEnum] = rolesLogin): Option[Person] = header match {
//    case Seq(header) if !header.isEmpty => AuthenticationCache.find(header) match {
//      case Some((user, userRoles)) =>
//        info(s"Auhtenticated ${user.fullname} ${userRoles} $header")
//        if (roles.subsetOf(userRoles)) {
//          info(s"Authenticated enough roles ${userRoles} >= ${roles}")
//          Some(user)
//        } else {
//          warn(s"Not authenticated not enough roles ${userRoles} < ${roles}")
//          None
//        }
//      case None =>
//        warn(s"Not authenticated unknown header $header")
//        None
//    }
//    case _ =>
//      warn(s"Not authenticated no header")
//      None
//  }
//
//  def Authenticated(roles: Set[RoleEnum] = rolesLogin)(f: AuthenticatedRequest => SimpleResult) = DBAction { implicit rs =>
//    isAuthenticated(rs.request.headers.getAll(AuthTokenHeader), roles) match {
//      case Some(user) => f(AuthenticatedRequest(user, rs))
//      case _ => Unauthorized
//    }
//  }
//
//  // returns an authToken
//  def login = DBAction { implicit rs =>
//    implicit val request = rs.request
//    val login = parse[Login]
//    info(s"Login attempt ${login.copy(password = "***")}")
//    findByUsernamePassword(login.username, login.password) match {
//      case Some((user, userRoles)) =>
//        if (userRoles.contains(RoleEnum.Login)) {
//          val authToken = UUID.randomUUID().toString()
//          updateToken(user, authToken)
//          val json = Json.obj(
//            AuthToken -> authToken,
//            Admin -> userRoles.contains(RoleEnum.Admin))
//          val maxAge = if (login.rememberme) Some(TimeUnit.DAYS.toSeconds(30).toInt) else None
//          info(s"Login successful, user ${user.fullname}")
//          Ok(json).withCookies(
//            Cookie(name = AuthToken, value = authToken, httpOnly = false, maxAge = maxAge))
//        } else {
//          warn(s"Login failed, user ${user.fullname} has no login role")
//          Unauthorized
//        }
//      case None =>
//        warn(s"Login failed, no user found for login")
//        Unauthorized
//    }
//  }
//
//  def logout = Authenticated() { implicit urs =>
//    info(s"Logout ${urs.user.fullname}")
//    implicit val session = urs.rs.session
//    deleteAuthToken(urs.user)
//    Redirect("/").discardingCookies(DiscardingCookie(name = AuthToken))
//  }
//
//  def isUser = DBAction { implicit rs =>
//    info("isUser")
//    isAuthenticated(rs.request.headers.getAll(AuthTokenHeader), rolesLogin) match {
//      case Some(user) => Ok
//      case _ => Unauthorized
//    }
//  }
//
//  def isAdmin = DBAction { implicit rs =>
//    info("isAdmin")
//    isAuthenticated(rs.request.headers.getAll(AuthTokenHeader), rolesAdmin) match {
//      case Some(user) => Ok
//      case _ => Unauthorized
//    }
//  }
//
//}
