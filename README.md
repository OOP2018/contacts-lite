## Sample Project for ORMLite

## Usage

The main class is `ContactsApp.java`. Before you run it, set two
variables in the file:
```java
// Name of a *directory* on your machine where database files will be saved.
// The directory must already exist.
private static final String DATABASE_URL = "jdbc:h2:/home/jim/temp/h2/contacts";
// Try to create database tables at startup? (Does nothing if tables exist.)
private static final boolean CREATE_TABLES = true;
```

For Windows you can use **forward slash** (/) as path separator.


## Files you need

Add these to your project **build path**.

* lib/ormlite-core-5.1.jar
* lib/ormlite-jdbc-5.1.jar
* lib/h2-2.1.197.jar - JDBC driver and related files for H2 database

## Reference

* [ORMLite.com](https://ormlite.com) home for ORMLite software and documentation.
* [H2 Database](http://www.h2database.com/) the H2 embedded database.

