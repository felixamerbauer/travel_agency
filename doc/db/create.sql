BEGIN;

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS airlines CASCADE;
DROP TABLE IF EXISTS hotelgroups CASCADE;
DROP TABLE IF EXISTS extHotels CASCADE;
DROP TABLE IF EXISTS extFlights CASCADE;
DROP TABLE IF EXISTS extHotelsLastmodified CASCADE;
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
  fullName TEXT NOT NULL
);


CREATE TABLE products (
  id             SERIAL PRIMARY KEY,
  fromLocationId INTEGER REFERENCES locations NOT NULL,
  toLocationId   INTEGER REFERENCES locations NOT NULL,
  archived       BOOLEAN NOT NULL,
  UNIQUE(fromLocationId, toLocationId)
);

CREATE TABLE orders (
  id           SERIAL PRIMARY KEY,
  customerId   INTEGER REFERENCES customers NOT NULL,
  productId    INTEGER REFERENCES products NOT NULL,
  hotelName    TEXT NOT NULL,
  hotelAddress TEXT NOT NULL,
  personCount  INTEGER NOT NULL CHECK (personCount >= 0),
  roomOrderId  TEXT NOT NULL,
  toFlight     TEXT NOT NULL,
  fromFlight   TEXT NOT NULL,
  startDate    TIMESTAMP NOT NULL,
  endDate      TIMESTAMP NOT NULL,
  price        INTEGER NOT NULL CHECK (price >= 0),
  currency     CHAR(3) NOT NULL
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
  locationId     INTEGER NOT NULL REFERENCES locations,
  startDate      DATE NOT NULL,
  endDate        DATE NOT NULL,
  personCount    INTEGER NOT NULL, -- how many persons can sleep in this room
  availableRooms INTEGER NOT NULL,  -- how many rooms of this type are available at the moment
  price          INTEGER NOT NULL CHECK (price >= 0),
  currency       CHAR(3) NOT NULL
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

CREATE TABLE extHotelsLastmodified (
  id           SERIAL PRIMARY KEY,
  lastModified TIMESTAMP NOT NULL,
  tmp          BOOLEAN
);


CREATE TABLE extFlightsLastmodified (
  id           SERIAL PRIMARY KEY,
  lastModified TIMESTAMP NOT NULL,
  tmp          BOOLEAN
);

END;