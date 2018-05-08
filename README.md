## Sample Project for ORMLite

This project is an example of how to save objects to a
database using Object Relational Mapping (ORM) and the
the [ORMLite][ORMLite] framework.

ORM performs these operations:

* **persist** (save) objects to a database table by saving each attribute in a table field
* **retrieve** (recreate) objects from data in a database table, by inserting the field values into the object's attributes
* **query** database and **search** for objects using some search criteria
* **delete** stored objects from a database
* manage object identities, so that each object has a unique identity and you don't create two copies of the same object

ORM relieves the programmer of a lot of boring, repetitive programming required to directly save/retrieve object data in a database.

You need 4 things to use ORM:

1. JAR files for the ORM framework
2. A database driver.   For a client-server database like MySQL you also need a database server. For an embedded database like [H2][H2] or SqlLite, no server is needed.
3. Add annotations to your code to tell the framework which classes are saved in which tables.
4. Create a "Data Access Object" (DAO) that performs the ORM operations.  The ORM framework makes this easy.

This example uses [ORMLite][ORMLite]

## Usage

The main class is `ContactsApp.java`. 

1. Before you run it, set the variables `DATABASE_URL` and `CREATE_TABLES`.  The DATABASE_URL should be a file inside an empty directory where you want H2 to save the database files.  In this example `/home/jim/h2` is the directory, and `contacts` is the basename for database files:

```java
// Name of a directory and base name of database files created in that directory.
// In this example, "/home/jim/h2/" is the directory, "contacts" is base name
// The directory must already exist.
private static final String DATABASE_URL = "jdbc:h2:/home/jim/h2/contacts";
// Try to create database tables at startup? (Does nothing if tables already exist.)
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

`Contact.java` is the class of objects we want to persist. Such objects are called "entitites". It has ORMLite annotations to define what should be saved, and where.

```java
@DatabaseTable(tableName="contacts")
public class Contact {
    // The "id" will be the databse table primary key value
    @DatabaseField(generatedId=true)
    private Long id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String telephone;
    @DatabaseField
    private String email;

    public Contact() {
         // ORM requires a no-argument constructor
         // may be "package" scope
    }
    /** The real constructor. */
    public Contact(String name, String telephone, String email) {
        // the id is assigned by the database, 
        // so don't set it here.
        this.name = name;
        this.telephone = telephone;
        this.email = email;
     }

     ... other methods, such as getName(), setName(), equals
}
```

In `Contact` the ORMLite annotations are used, but you can use standard JPA annotations instead (see ORMLite User's Guide, Chapter 2).

ORMLite creates a *Data Access Object* for your entity classes.
In the main class, we write:
```java
ConnectionSource connSource = new JdbcConnectionSource(DATABASE_URL);
Dao<Contact,Long> contactDao = 
         DaoManager.createDao(connectionSource, Contact.class);
```

You create a connection to the database,
then use it to instantiate a DAO for your entity class using `DaoManager`.
The two type parameters `Dao<Type,Key>` are the entity class name and the type of the table primary key (id field).  In `Contact` we used a `Long` as the id field.

In a real application you probably want to write your own DAO so that you can add custom search methods.  With ORMLite your DAO should extend `BaseDaoImpl`.  BaseDaoImpl provides the basic `create()`, `delete()`, update()`, and query methods, so you only need to write your custom code. 

You can also use a PooledConnectionSource for many connections if you need them.

### What the DAO Does: CRUD Operations

The Dao objects (like `contactDao`) provide basic database  operations

| contactDao Method | What is does                 |
|:------------------|:-----------------------------|
| create(contact) | save a contact object to the database |
| update(contact) | update record for existing contact with values from object |
| query           | query and retrieve objects |
| iterator()      | get all contacts from the database, as an Iterator |
| delete(contact) | delete a contact from the database |

The basic operations Create, Retrieve, Update, and Delete form the acronym CRUD, a common term in software design.

To save a new contact to the database, simply write:

```java
Contact contact = new Contact("Bill Gates","14145550001","bill@microsoft.com");
contactDao.create( contact );
// the database assigns an id to the object:
System.out.println("Bill saved with id "+contact.getId());
```

Finding or querying objects in the database is the most complex operation, since there many ways you might want to "search" for something.  And the search criteria depend on the type of entity.  

ORMLite provides a `QueryBuilder` class that can create just about any query.  This is better than using raw SQL, like "SELECT * from contacts WHERE name LIKE ...", which is error-prone and vulnerable to SQL Injection attacks.

If we want to find all contacts that use Gmail we could write:
```java
QueryBuilder<Contact,Long> qb = contactDao.queryBuilder();
qb = qb.where().like("email","%@gmail.com");
// perform the query. Matches returned as a List.
List<Contact> results = qb.query();

System.out.printf("found %d gmail users\n", results.size());
for(Contact c: results) System.out.println( c.getName() );
```


## How To Limit Log Messages?

ORMLite has a built-in Logging facility named LocalLog.  To set the minimum several of log messages see:
[http://ormlite.com/javadoc/ormlite-core/com/j256/ormlite/logger/LocalLog.html](http://ormlite.com/javadoc/ormlite-core/com/j256/ormlite/logger/LocalLog.html)

## Code Improvement: Use Properties for Configuration

You should not hard-code configuration information in your Java code.
Use a properties file and the `util.PropertyManager` class to get configuration values from a properties file.  For this app, the properties file is `contacts.config` in the src directory (it will be copied to the `bin` directory during project build).

The property names can be anything you like; this example uses the standard names from JDBC:

```
# A properties file for configuration values
jdbc.url = jdbc:h2:/home/jim/h2/contacts
jdbc.driver = org.h2.Driver
# For a client-server database you need a user and password.
# This isn't needed for H2, but included as example.
jdbc.user =
jdbc.password =
```

## References

* [ORMLite.com][ORMLite] home for ORMLite software and documentation.
* [H2 Database][H2] the H2 embedded database.
* [Intro to Database](https://skeoop.github.io/database/) slides introducing database and ORM concepts.

---

[ORMLite]: https://ormlite.com
[H2]: http://www.h2database.com

