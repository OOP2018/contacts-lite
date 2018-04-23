## Sample Project for ORMLite

Simple application showing how to persist and retrieve objects
using a database and ORMLite.  The objects to persist are Contacts
with these attributes:

<table border=1 width="25% ">
<tr>
<th>Contact</th>
</tr>
<tr valign="top" markdown="span">
<td>
id:  Long    
name: String
email: String
telephone: String
</td>
</tr>

## Usage


1. Edit  `ContactsApp.java` and modify the values of `DATABASE_URL` and `CREATE_TABLES`:
```java
// The Name of a directory + base name of database files created in that directory.
// In this example, ".../temp/h2/" is the directory, "contacts" is base name
// The directory must already exist.
private static final String DATABASE_URL = "jdbc:h2:/home/jim/temp/h2/contacts";
// Try to create database tables at startup? (Does nothing if tables exist.)
private static final boolean CREATE_TABLES = true;
```
For MS Windows use **forward slash** (/) as path separator.

2. Optionally, edit code in `addContacts()` method to add your own contacts.

3. Compile and run the `ContactsApp` class as a Java application.

## Files you need

Add these to your project **build path**.

* lib/ormlite-core-5.1.jar
* lib/ormlite-jdbc-5.1.jar
* lib/h2-2.1.197.jar - JDBC driver and related files for H2 database

## What the Application Shows

`Contact.java` has ORMLite annotations for persisting objects to a database.  The annotations tell ORMLite what *table* to use, which field is the *primary key* (identity), and other other fields to save in the database table.  If you prefer, you can use standard JPA annotations instead of ORMLite's own annotations (see ORMLite User's Guide, Chapter 2).

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

ORMLite creates a *Data Access Object* (DAO) for your entity classes.
The DAO is how you save, update, find and retrieve, or delete object data
in the database.

To create a DAO, in `ContactsApp` we write:
```java
ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL);
Dao<Contact,Long> contactDao =
                  DaoManager.createDao(connectionSource, Contact.class);
```
The two type parameters in `Dao<Type,Key>` are the entity class name and the type of the primary key field (id).  In `Contact` we used a `Long` as the id field.

ORMLite gives you the flexibility to:

* define your own DAO classes as subclasses of `BaseDaoImpl` in case you want to add new functionality to the DAO.
* use a PooledConnectionSource for many database connections, if you need them.

### DAO for CRUD Operations

The Dao objects (like `contactDao`) provide basic database operations

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

Finding or querying objects in the database is the most complex operation, since there many ways you might want to "search" for something.  And the search criteria depend on the attributes of each entity.
To find objects by name using a QueryBuilder:

```java
QueryBuilder<Contact,Long> qb = contactDao.queryBuilder();
// search contacts WHERE name equals "Jim"
qb.where().eq("name", "Jim");
// perform the query. It may return 0 or more results
List<Contact> result = qb.query();
```


## How To Limit Log Messages?

ORMLite has a built-in Logging facility named LocalLog. By default it prints all messages on the console, which is a good way to learn what ORMLite is doing, but very verbose.

 To set the minimum level of log messages see:
[http://ormlite.com/javadoc/ormlite-core/com/j256/ormlite/logger/LocalLog.html](http://ormlite.com/javadoc/ormlite-core/com/j256/ormlite/logger/LocalLog.html)

You can set the logging level via a System property; the property must be set *before* the Logger is instantiated.
To ensure the System property is set before the `main` method runs, add a **static block** to ContactsApp:
```java
// Limit minimum severity of log messages printed on console.
static {
    System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "error");
}
```


## Code Improvement

You should not hard-code configuration information in your Java code, such as the database URL, database username, or passwords.
Use a properties file for such data.
The Contacts app has a file named `contacts.config` in the top-level source directory (so it will be copied to "bin" which is on the Java classpath).  The file contains:
```shell
# Location of the database.
database.url = jdbc:h2:/home/jim/temp/h2/contacts

# Create tables on start-up if they don't exist?
createtables = true
```
A Properties file contains "key = value" lines, without quotes around either the key or value.  Blank lines and comment (#) lines are ignored.
Use the `util.PropertyManager` class to get configuration values from the properties file instead of hard-coding the values in Java code.

For example:
```java
String DATABASE_URL = PropertyManager.getProperty("database.url");
```


## Reference

* [ORMLite.com](https://ormlite.com) home for ORMLite software and documentation.
* [H2 Database](http://www.h2database.com/) the H2 embedded database.
