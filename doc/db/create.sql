drop table if exists "orders";
drop table if exists "airlines";
drop table if exists "hotelgroups";
drop table if exists "extHotelRooms";
drop table if exists "extFlight";
drop table if exists "products";
drop table if exists "customers";
drop table if exists "users";
drop table if exists "locations";


create table "users" ("id" SERIAL NOT NULL PRIMARY KEY,"email" VARCHAR(254) NOT NULL,"passwordHash" VARCHAR(254) NOT NULL);
create unique index "users_email_UNIQUE" on "users" ("email");
create table "customers" ("id" SERIAL NOT NULL PRIMARY KEY,"user" INTEGER NOT NULL,"firstName" VARCHAR(254) NOT NULL,"lastName" VARCHAR(254) NOT NULL,"birthDate" VARCHAR(254) NOT NULL,"sex" VARCHAR(254) NOT NULL,"street" VARCHAR(254) NOT NULL,"zipCode" VARCHAR(254) NOT NULL,"city" VARCHAR(254) NOT NULL,"country" VARCHAR(254) NOT NULL,"phoneNumber" VARCHAR(254) NOT NULL,"creditCardCompany" VARCHAR(254) NOT NULL,"creditCardNumber" VARCHAR(254) NOT NULL,"creditCardExpireDate" VARCHAR(254) NOT NULL,"creditCardVerificationCode" VARCHAR(254) NOT NULL);
alter table "customers" add constraint "User_FK" foreign key("user") references "users"("id") on update NO ACTION on delete NO ACTION;
create table "locations" ("id" SERIAL NOT NULL PRIMARY KEY,"iataCode" VARCHAR(254) NOT NULL,"fullName" VARCHAR(254) NOT NULL);
create table "products" ("id" SERIAL NOT NULL PRIMARY KEY,"fromLocationId" INTEGER NOT NULL,"toLocationId" INTEGER NOT NULL,"archived" BOOLEAN NOT NULL);
alter table "products" add constraint "FromLocation_FK" foreign key("fromLocationId") references "locations"("id") on update NO ACTION on delete NO ACTION;
alter table "products" add constraint "ToLocation_FK" foreign key("toLocationId") references "locations"("id") on update NO ACTION on delete NO ACTION;
create table "orders" ("id" SERIAL NOT NULL PRIMARY KEY,"customerId" INTEGER NOT NULL,"productId" INTEGER NOT NULL,"hotelName" VARCHAR(254) NOT NULL,"hotelAddress" VARCHAR(254) NOT NULL,"personCount" INTEGER NOT NULL,"roomOrderId" VARCHAR(254) NOT NULL,"toFlight" VARCHAR(254) NOT NULL,"fromFlight" VARCHAR(254) NOT NULL,"startDate" TIMESTAMP NOT NULL,"endDate" TIMESTAMP NOT NULL,"price" DOUBLE PRECISION NOT NULL,"currency" VARCHAR(254) NOT NULL);
alter table "orders" add constraint "Product_FK" foreign key("productId") references "products"("id") on update NO ACTION on delete NO ACTION;
alter table "orders" add constraint "Customer_FK" foreign key("customerId") references "customers"("id") on update NO ACTION on delete NO ACTION;
create table "airlines" ("id" SERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"apiUrl" VARCHAR(254) NOT NULL);
create table "hotelgroups" ("id" SERIAL NOT NULL PRIMARY KEY,"name" VARCHAR(254) NOT NULL,"apiUrl" VARCHAR(254) NOT NULL);
create table "extHotelRooms" ("id" SERIAL NOT NULL PRIMARY KEY,"hotelShortName" VARCHAR(254) NOT NULL,"hotelName" VARCHAR(254) NOT NULL,"locationId" INTEGER NOT NULL,"startDate" TIMESTAMP NOT NULL,"endDate" TIMESTAMP NOT NULL,"personCount" INTEGER NOT NULL,"availableRooms" INTEGER NOT NULL);
alter table "extHotelRooms" add constraint "Location_FK" foreign key("locationId") references "locations"("id") on update NO ACTION on delete NO ACTION;
create table "extFlight" ("id" SERIAL NOT NULL PRIMARY KEY,"airlineShortName" VARCHAR(254) NOT NULL,"airlineName" VARCHAR(254) NOT NULL,"fromLocationId" INTEGER NOT NULL,"toLocationId" INTEGER NOT NULL,"dateTime" TIMESTAMP NOT NULL,"availableSeats" INTEGER NOT NULL);
alter table "extFlight" add constraint "FromLocation_FK" foreign key("fromLocationId") references "locations"("id") on update NO ACTION on delete NO ACTION;
alter table "extFlight" add constraint "ToLocation_FK" foreign key("toLocationId") references "locations"("id") on update NO ACTION on delete NO ACTION;