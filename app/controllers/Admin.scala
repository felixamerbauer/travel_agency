package controllers

import controllers.Security.authenticated
import controllers.Security.loginForm
import db.QueryBasics.qAirline
import db.QueryBasics._
import db.QueryLibrary._
import models.Airline
import models.Hotelgroup
import models.TAirline
import models.THotelgroup
import models.Product
import play.api.Logger.info
import play.api.Logger.warn
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.Config.driver.simple.columnExtensionMethods
import play.api.db.slick.Config.driver.simple.productQueryToUpdateInvoker
import play.api.db.slick.Config.driver.simple.queryToQueryInvoker
import play.api.db.slick.Config.driver.simple.valueToConstColumn
import play.api.db.slick.DBAction
import play.api.mvc.Controller
import views.formdata.AdminAirlineFormData
import views.formdata.AdminHotelgroupFormData
import views.formdata.AdminProductFormData
import views.html.admin
import models.TProduct
import views.formdata.AdminCustomerFormData
import views.formdata.Commons._
import models.Customer
import models.TUser
import models.User
import models.TCustomer
import play.api.data.validation._
import scala.collection.mutable.Buffer
import play.api.data.FormError

object Admin extends Controller {

  private val airlineForm = Form(
    mapping(
      "id" -> text,
      "name" -> nonEmptyText,
      "apiURL" -> nonEmptyText)(AdminAirlineFormData.apply)(AdminAirlineFormData.unapply).
      verifying(AdminAirlineFormData.constraints))

  private val hotelgroupForm = Form(
    mapping(
      "id" -> text,
      "name" -> nonEmptyText,
      "apiURL" -> nonEmptyText)(AdminHotelgroupFormData.apply)(AdminHotelgroupFormData.unapply))

  private val productForm = Form(
    mapping(
      "id" -> text,
      "from" -> nonEmptyText,
      "to" -> nonEmptyText,
      "archived" -> boolean)(AdminProductFormData.apply)(AdminProductFormData.unapply).
      verifying(AdminProductFormData.constraints))

  private val customerForm = Form(
    mapping(
      "id" -> text,
      "userId" -> text,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> text,
      "password" -> text,
      "birthDate" -> text,
      "sex" -> text,
      "street" -> text,
      "zipCode" -> text,
      "city" -> text,
      "country" -> text,
      "phoneNumber" -> text,
      "creditCardCompany" -> text,
      "creditCardNumber" -> text,
      "creditCardExpireDate" -> text,
      "creditCardVerificationCode" -> text)(AdminCustomerFormData.apply)(AdminCustomerFormData.unapply))

  def airlines = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    val data = qAirline.to[Vector]
    Ok(admin.airlines(loginForm, authenticated, data))
  }

  def airline(id: Option[Int]) = DBAction { implicit rs =>
    id match {
      case Some(id) =>
        implicit val dbSession = rs.dbSession
        val data = qAirline.where(_.id === id).to[Vector]
        data match {
          case Seq(item) =>
            val filled = airlineForm.fill(new AdminAirlineFormData(item))
            Ok(admin.airline(loginForm, authenticated, filled))
          case _ =>
            warn(s"Couldn't find airline for id $id")
            Redirect(routes.Admin.airlines)
        }
      case None =>
        Ok(admin.airline(loginForm, authenticated, airlineForm))
    }
  }

  def airlineDelete(id: Int) = DBAction { implicit rs =>
    info(s"airlineDelete $id")
    implicit val dbSession = rs.dbSession
    qAirline.where(_.id === id).delete
    Redirect(routes.Admin.airlines)
  }

  def airlinePost() = DBAction { implicit rs =>
    val filledForm: Form[AdminAirlineFormData] = airlineForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form hat errors ${formWithErrors.data}")
        BadRequest(admin.airline(loginForm, authenticated, formWithErrors))
      },
      formData => {
        info(s"form is ok $formData")
        implicit val dbSession = rs.dbSession
        val airline = Airline(id = formData.id.toInt, name = formData.name, apiUrl = formData.apiURL)
        if (airline.id == -1)
          TAirline.forInsert.insert(airline)
        else
          qAirline.where(_.id === formData.id.toInt).update(airline)
        Redirect(routes.Admin.airlines)
      })
  }

  def customers = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    val data = qUserWithCustomer.to[Vector]
    Ok(admin.customers(loginForm, authenticated, data))
  }

  def customer(id: Option[Int]) = DBAction { implicit rs =>
    id match {
      case Some(id) =>
        implicit val dbSession = rs.dbSession
        val data = qUserWithCustomer(id).to[Vector]
        data match {
          case Seq(item) =>
            val (user, customer) = item
            val filled = customerForm.fill(new AdminCustomerFormData(user, customer))

            val sexesSelect = sexes + (if ("m" == customer.sex) ("Männlich" -> true) else ("Weiblich" -> true))
            Ok(admin.customer(loginForm, authenticated, filled, sexesSelect, creditCardCompanies + (customer.creditCardCompany -> true)))
          case _ =>
            warn(s"Couldn't find customer for id $id")
            Redirect(routes.Admin.customers)
        }
      case None =>
        Ok(admin.customer(loginForm, authenticated, customerForm, sexesFirstSelected, creditCardCompaniesFirstSelected))
    }
  }

  def customerPost() = DBAction { implicit rs =>
    val filledForm: Form[AdminCustomerFormData] = customerForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form hat errors ${formWithErrors.data}")
      },
      formData => {
        info(s"form is ok $formData")
        implicit val dbSession = rs.dbSession
        val customer = Customer(
          id = formData.id.toInt,
          userId = -1,
          firstName = formData.firstName,
          lastName = formData.lastName,
          birthDate = dateFormat.parseDateTime(formData.birthDate).toDateMidnight(),
          sex = formData.sex,
          street = formData.street,
          zipCode = formData.zipCode,
          city = formData.city,
          country = formData.country,
          phoneNumber = formData.phoneNumber,
          creditCardCompany = formData.creditCardCompany,
          creditCardNumber = formData.creditCardNumber,
          creditCardExpireDate = dateFormat.parseDateTime(formData.creditCardExpireDate).toDateMidnight(),
          creditCardVerificationCode = formData.creditCardVerificationCode)
        val user = User(
          id = formData.userId.toInt,
          email = formData.email,
          passwordHash = formData.password)
        if (customer.id == -1) {
          val userId = TUser.forInsert.insert(user)
          TCustomer.forInsert.insert(customer.copy(userId = userId))
        } else {
          qUser.where(_.id === formData.userId.toInt).update(user)
          qCustomer.where(_.id === formData.id.toInt).update(customer)
        }
      })
    Redirect(routes.Admin.customers)
  }

  def customerDelete(id: Int) = DBAction { implicit rs =>
    info(s"customerDelete $id")
    implicit val dbSession = rs.dbSession
    val userId = qCustomer.where(_.id === id).map(_.userId).to[Seq].head
    qCustomer.where(_.id === id).delete
    qUser.where(_.id === userId).delete
    Redirect(routes.Admin.customers)
  }

  def hotelgroups = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    val data = qHotelgroup.to[Vector]
    Ok(admin.hotelgroups(loginForm, authenticated, data))
  }

  def hotelgroup(id: Option[Int]) = DBAction { implicit rs =>
    id match {
      case Some(id) =>
        implicit val dbSession = rs.dbSession
        val data = qHotelgroup.where(_.id === id).to[Vector]
        data match {
          case Seq(item) =>
            val filled = hotelgroupForm.fill(new AdminHotelgroupFormData(item))
            Ok(admin.hotelgroup(loginForm, authenticated, filled))
          case _ =>
            warn(s"Couldn't find hotelgroup for id $id")
            Redirect(routes.Admin.hotelgroups)
        }
      case None =>
        Ok(admin.hotelgroup(loginForm, authenticated, hotelgroupForm))
    }
  }

  def hotelgroupPost() = DBAction { implicit rs =>
    val filledForm: Form[AdminHotelgroupFormData] = hotelgroupForm.bindFromRequest
    filledForm.fold(
      formWithErrors => {
        info(s"form hat errors ${formWithErrors.data}")
      },
      formData => {
        info(s"form is ok $formData")
        implicit val dbSession = rs.dbSession
        val hotelgroup = Hotelgroup(id = formData.id.toInt, name = formData.name, apiUrl = formData.apiURL)
        if (hotelgroup.id == -1)
          THotelgroup.forInsert.insert(hotelgroup)
        else
          qHotelgroup.where(_.id === formData.id.toInt).update(hotelgroup)
      })
    Redirect(routes.Admin.hotelgroups)
  }

  def hotelgroupDelete(id: Int) = DBAction { implicit rs =>
    info(s"hotelgroupDelete $id")
    implicit val dbSession = rs.dbSession
    qHotelgroup.where(_.id === id).delete
    Redirect(routes.Admin.hotelgroups)
  }

  
  
  def products = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    val data = qProductsWithLocation.to[Vector]
    Ok(admin.products(loginForm, authenticated, data))
  }

  def product(id: Option[Int]) = DBAction { implicit rs =>
    id match {
      case Some(id) =>
        implicit val dbSession = rs.dbSession
        val data = qProductsWithLocation.where(_._1.id === id).to[Vector]
        data match {
          case Seq(productFromTo) =>
            val (product, from, to) = productFromTo
            val filled = productForm.fill(new AdminProductFormData(product, from, to))
            val locationsFrom = locations + (from.fullName -> true)
            val locationsTo = locations + (to.fullName -> true)
            Ok(admin.product(loginForm, authenticated, filled, locationsFrom, locationsTo))
          case _ =>
            warn(s"Couldn't find product for id $id")
            Redirect(routes.Admin.products)
        }
      case None =>
        Ok(admin.product(loginForm, authenticated, productForm, locationsFirstSelected, locationsFirstSelected))
    }
  }

  def productPost() = DBAction { implicit rs =>
    val filled: Form[AdminProductFormData] = productForm.bindFromRequest
    filled.fold(
      formWithErrors => {
        info(s"form hat errors ${formWithErrors.data}")
        // TODO preselect latest selection
        BadRequest(admin.product(loginForm, authenticated, formWithErrors, locationsFirstSelected, locationsFirstSelected))
      },
      formData => {
        info(s"form is ok $formData")
        implicit val dbSession = rs.dbSession
        val fromId = qLocation.where(_.fullName === formData.from).map(_.id).to[Seq].head
        val toId = qLocation.where(_.fullName === formData.to).map(_.id).to[Seq].head
        val product = Product(id = formData.id.toInt, fromLocationId = fromId, toLocationId = toId, archived = formData.archived)
        if (product.id == -1)
          TProduct.forInsert.insert(product)
        else
          qProduct.where(_.id === formData.id.toInt).update(product)
        Redirect(routes.Admin.products)
      })
  }

  def productDelete(id: Int) = DBAction { implicit rs =>
    info(s"productDelete $id")
    implicit val dbSession = rs.dbSession
    qProduct.where(_.id === id).delete
    Redirect(routes.Admin.products)
  }


  def flights = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    val data = qFlightsWithLocation.to[Vector]
    Ok(admin.flights(loginForm, authenticated, data))
  }

  def hotels = DBAction { implicit rs =>
    implicit val dbSession = rs.dbSession
    val data = qHotelWithLocation.to[Vector]
    Ok(admin.hotels(loginForm, authenticated, data))
  }

}