BEGIN;

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS airlines CASCADE;
DROP TABLE IF EXISTS hotelgroups CASCADE;
DROP TABLE IF EXISTS extHotels CASCADE;
DROP TABLE IF EXISTS extHotelBookings CASCADE;
DROP TABLE IF EXISTS extHotelsLastmodified CASCADE;
DROP TABLE IF EXISTS extFlights CASCADE;
DROP TABLE IF EXISTS extFlightBookings CASCADE;
DROP TABLE IF EXISTS extFlightsLastmodified CASCADE;

CREATE TABLE users (
  id           SERIAL PRIMARY KEY,
  email        TEXT UNIQUE NOT NULL,
  passwordHash TEXT NOT NULL
);

CREATE TABLE customers (
  id                         SERIAL PRIMARY KEY,
  userId                     INTEGER REFERENCES users NOT NULL,
  firstName                  TEXT NOT NULL,
  lastName                   TEXT NOT NULL,
  birthDate                  DATE NOT NULL,
  sex                        CHAR(1) CHECK (sex IN ('m','f')) NOT NULL,
  street                     TEXT NOT NULL,
  zipCode                    TEXT NOT NULL,
  city                       TEXT NOT NULL,
  country                    TEXT NOT NULL,
  phoneNumber                TEXT,
  creditCardCompany          TEXT,
  creditCardNumber           TEXT,
  creditCardExpireDate       DATE,
  creditCardVerificationCode TEXT
);

CREATE TABLE locations (
  id       SERIAL PRIMARY KEY,
  iataCode CHAR(3) NOT NULL,
  fullName TEXT NOT NULL,
  start    BOOLEAN NOT NULL,
  UNIQUE(iataCode),
  UNIQUE(fullName)
);


CREATE TABLE products (
  id             SERIAL PRIMARY KEY,
  fromLocationId INTEGER REFERENCES locations NOT NULL,
  toLocationId   INTEGER REFERENCES locations NOT NULL,
  archived       BOOLEAN NOT NULL,
  UNIQUE(fromLocationId, toLocationId)
);

CREATE TABLE orders (
  id                   SERIAL PRIMARY KEY,
  customerId           INTEGER REFERENCES customers NOT NULL,
  fromLocation         TEXT NOT NULL,
  toLocation           TEXT NOT NULL,
  hotelName	           TEXT NOT NULL,
  hotelId	           INTEGER NOT NULL,
  outwardFlightAirline TEXT NOT NULL,
  outwardFlightId      INTEGER NOT NULL,
  inwardFlightAirline  TEXT NOT NULL,
  inwardFlightId       INTEGER NOT NULL,
  adults               INTEGER NOT NULL CHECK (adults >= 0),
  children             INTEGER NOT NULL CHECK (children >= 0),
  startDate            TIMESTAMP NOT NULL,
  endDate              TIMESTAMP NOT NULL,
  price                INTEGER NOT NULL CHECK (price >= 0),
  currency             CHAR(3) NOT NULL
);

CREATE TABLE airlines (
  id     SERIAL PRIMARY KEY,
  name   TEXT NOT NULL,
  apiUrl TEXT NOT NULL
);

CREATE TABLE hotelgroups (
  id     SERIAL PRIMARY KEY,
  name   TEXT NOT NULL,
  apiUrl TEXT NOT NULL
);

--- Tables for external services (Airlines and Hotelgroups)

CREATE TABLE extHotels (
  id             SERIAL PRIMARY KEY,
  apiUrl         TEXT NOT NULL, -- for differentiation of REST services
  hotelName      TEXT NOT NULL,
  description    TEXT NOT NULL,
  category		 INTEGER NOT NULL CHECK (category >= 1 AND category <= 5),
  locationId     INTEGER NOT NULL REFERENCES locations,
  startDate      DATE NOT NULL,
  endDate        DATE NOT NULL,
  availableRooms INTEGER NOT NULL,  -- how many rooms of this type are available at the moment
  price          INTEGER NOT NULL CHECK (price >= 0),
  currency       CHAR(3) NOT NULL
);

CREATE TABLE extHotelBookings (
  id             SERIAL PRIMARY KEY,
  extHotelId     INTEGER REFERENCES extHotels NOT NULL,
  rooms		     TEXT NOT NULL
);

CREATE TABLE extHotelsLastmodified (
  id           SERIAL PRIMARY KEY,
  lastModified TIMESTAMP NOT NULL,
  tmp          BOOLEAN
);

CREATE TABLE extFlights (
  id               SERIAL PRIMARY KEY,
  apiUrl           TEXT NOT NULL, -- for differentiation of REST services
  airlineName      TEXT NOT NULL,
  fromLocationId   INTEGER NOT NULL REFERENCES locations,
  toLocationId     INTEGER NOT NULL REFERENCES locations,
  dateTime         TIMESTAMP NOT NULL,
  availableSeats   INTEGER NOT NULL CHECK (availableSeats >= 0),  -- how many seats for this flight are available at the moment
  price            INTEGER NOT NULL CHECK (price >= 0),
  currency         CHAR(3) NOT NULL
);

CREATE TABLE extFlightBookings (
  id             SERIAL PRIMARY KEY,
  extFlightId    INTEGER REFERENCES extFlights NOT NULL,
  seats		     TEXT NOT NULL
);

CREATE TABLE extFlightsLastmodified (
  id           SERIAL PRIMARY KEY,
  lastModified TIMESTAMP NOT NULL,
  tmp          BOOLEAN
);

END;