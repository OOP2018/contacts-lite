## Sample Project for ORMLite

## Usage

The main class is `ContactsApp.java`. 

1. Before you run it, set the variables `DATABASE_URL` and `CREATE_TABLES`:
```java
// The Name of a directory + base name of database files created in that directory.
// In this example, ".../temp/h2/" is the directory, "contacts" is base name
// The directory must already exist.
private static final String DATABASE_URL = "jdbc:h2:/home/jim/temp/h2/contacts";
// Try to create database tables at startup? (Does nothing if tables exist.)
private static final boolean CREATE_TABLES = true;
```
For Windows you can use **forward slash** (/) as path separator.

2. Optionally, edit code in `addContacts()` to add your own contacts.

3. Run the `ContactsApp` class as Java application.

## Files you need

Add these to your project **build path**.

* lib/ormlite-core-5.1.jar
* lib/ormlite-jdbc-5.1.jar
* lib/h2-2.1.197.jar - JDBC driver and related files for H2 database

## What the Application Shows

`Contact.java` has ORMLite annotations for persisting objects to a database.  The annotations tell ORMLite what *table* to use, which field is the *primary key* (identity), and other other fields to save in database table.  If you prefer, you can use standard JPA annotations instead of ORMLite's own annotations (see ORMLite User's Guide, Chapter 2).

```java
@DatabaseTable(tableName="contacts")
public class Contact {
    // this field is the primary key or "identity" for saved objects
    @DatabaseField(generatedId=true)
    private Long id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String email;
    ...
}
```

ORMLite creates a *Data Access Object* for your entity classes.
The DAO has methods to save, update, find, or data object data in the database.

To create a DAO, in `ContactsApp` we write:
```java
ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL);
Dao<Contact,Long> contactDao = 
                  DaoManager.createDao(connectionSource, Contact.class);
```
The two type parameters in `Dao<Type,Key>` are the entity class name and the type of the table primary key (id field).  In `Contact` we used a `Long` as the id field.

ORMLite gives you the flexibility to:

* define your own DAO classes as subclasses of `BaseDaoImpl` in case you want to add new functionality.
* use a PooledConnectionSource for many connections if you need them.

### DAO for CRUD Operations

The Dao objects (like `contactDao`) provide basic CRUD operations

| contactDao Method | What is does                 |
|:------------------|:-----------------------------|
| create(contact)   | save a contact to the database |
| update(contact)   | update record for existing contact with values from object |
| find or query     | query and retrieve objects |
| iterator()        | get all contacts from the database, as an Iterator |
| delete(contact)   | delete a contact from the database |

For example, to save an object's data as a row in the "contacts" table:
```java
Contact fatalai = new Contact("Fatalaijon","091-5551212","fatalai@gmail.com");
contactDao.create( fatalai );
// show the id assigned to this object by database
System.out.println("Fatalai saved. His id is "+fatalai.getId() );
```

Finding or querying objects in the database is the most complex operation, since there many ways you might want to "search" for something.  And the search criteria depend on the type of entity.
To find objects by name using a QueryBuilder:

```java
QueryBuilder<Contact,Long> qb = contactDao.queryBuilder();
// search contacts WHERE name equals "Jim"
qb.where().eq("name", "Jim');
// perform the query. It may return 0 or more results
List<Contact> result = qb.query();
```


## How To Limit Log Messages?

ORMLite has a built-in Logging facility named LocalLog.  To set the minimum several of log messages see:
[http://ormlite.com/javadoc/ormlite-core/com/j256/ormlite/logger/LocalLog.html](http://ormlite.com/javadoc/ormlite-core/com/j256/ormlite/logger/LocalLog.html)

## Code Improvement

You should not hard-code configuration information in your Java code.
Use a properties file and the `util.PropertyManager` class to get configuration values from a properties file.

For example:
```java
String DATABASE_URL = PropertyManager.getProperty("database.url");
```

## Reference

* [ORMLite.com](https://ormlite.com) home for ORMLite software and documentation.
* [H2 Database](http://www.h2database.com/) the H2 embedded database.

