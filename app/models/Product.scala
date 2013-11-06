package models

import play.api.db.slick.Config.driver.simple._

object TProduct extends Table[Product]("products") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def fromLocationId = column[Int]("fromLocationId")
  def toLocationId = column[Int]("toLocationId")
  def archived = column[Boolean]("archived")
  def baseProjection = fromLocationId ~ toLocationId ~ archived
  override def * = id ~: baseProjection <> (Product, Product.unapply _)
  def forInsert = baseProjection <> (
    { t => Product(-1, t._1, t._2, t._3) },
    { (p: Product) => Some((p.fromLocationId, p.toLocationId, p.archived)) })
  def autoInc = forInsert returning id

  def fromLocation = foreignKey("FromLocation_FK", fromLocationId, TLocation)(_.id)
  def toLocation = foreignKey("ToLocation_FK", toLocationId, TLocation)(_.id)
}

case class Product(
  id: Int = -1,
  fromLocationId: Int,
  toLocationId: Int,
  archived: Boolean)

  