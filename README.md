## Sample Project for ORMLite

## Usage

The main class is `ContactsApp.java`. 

1. Before you run it, set two variables in the file:
```java
// Name of a *directory* on your machine where database files will be saved.
// The directory must already exist.
private static final String DATABASE_URL = "jdbc:h2:/home/jim/temp/h2/contacts";
// Try to create database tables at startup? (Does nothing if tables exist.)
private static final boolean CREATE_TABLES = true;
```
For Windows you can use **forward slash** (/) as path separator.

2. Optionally, edit code in `addContacts()` to add your own contacts.

3. Run the `ContactsApp` class as Java application.

## What it Shows

The `Contact.java` has ORMLite annotations for persisting objects using a datbase.  These objects are "entitites".

In `Contact` the ORMLite annotations are used, but you can use standard JPA annotations instead (see ORMLite User's Guide, Chapter 2).

ORMLite creates a *Data Access Object* for your entity classes.
In the main class, we wrote:
```java
ConnectionSource connSource = new JdbcConnectionSource(DATABASE_URL);
Dao<Contact,Long> contactDao = 
         DaoManager.createDao(connectionSource, Contact.class);
```
You create a connection to the database,
then use it to instantiate a DAO for your entity class using `DaoManager`.
ORMLite gives you the flexibility to:

* define your own DAO classes as subclasses of `BaseDaoImpl` in case you want to add new functionality.
* use a PooledConnectionSource for many connections if you need them.

The Dao objects (like `contactDao`) provide basic CRUD operations

| contactDao Method | What is does                 |
|:------------------|:-----------------------------|
| create(contact) | save a contact to the database |
| update(contact) | update record for existing contact with values from object |
| find or query   | query and retrieve objects |
| iterator()      | get all contacts from the database, as an Iterator |
| delete(contact) | delete a contact from the database |

Finding or querying objects in the database is the most complex operation, since there many ways you might want to "search" for something.  And the search criteria depend on the type of entity.


## Files you need

Add these to your project **build path**.

* lib/ormlite-core-5.1.jar
* lib/ormlite-jdbc-5.1.jar
* lib/h2-2.1.197.jar - JDBC driver and related files for H2 database

## Reference

* [ORMLite.com](https://ormlite.com) home for ORMLite software and documentation.
* [H2 Database](http://www.h2database.com/) the H2 embedded database.

