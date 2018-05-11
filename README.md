## Sample Project for ORMLite

This project is an example of how to save objects to a
database using Object Relational Mapping (ORM) and the
the [ORMLite][ORMLite] framework.
It also shows how to use an embedded [H2][H2] database
that can be easily created and initialized.

ORM performs these operations:

* **persist** (save) objects to a database table by saving each attribute in a table field
* **retrieve** (recreate) objects from data in a database table, by inserting the field values into the object's attributes
* **query** database and **search** for objects using some search criteria
* **delete** stored objects from a database
* manage object identities, so that each object has a unique identity and you don't create two copies of the same object

ORM eliminates a lot of boring, repetitive programming needed to directly save objects as fields in a database, or create objects from a database. ORM does this for you.

You need 4 things to use ORM:

1. JAR files for the ORM framework
2. A database driver.   For a client-server database like MySQL you also need a database server. For an embedded database like [H2][H2] or SqlLite, no server is needed.
3. Add annotations to your code to tell the framework which classes to save in the database, and the table names.
4. Create a "Data Access Object" (DAO) that performs the ORM operations.  The ORM framework makes this easy.

## How to Build and Run

1. Clone this repository.
2. Create a directory for the database files, for example /temp/h2/.
3. Edit `src/contacts.config` and specify the database path.  Include "contacts" at the end of the path, which is the basename for files created by H2.
For Windows you should use **forward slash** (/) in paths, as shown here.
```shell
# Name of a directory and base name of database files created in that directory.
# In this example, "/temp/h2/" is the directory, "contacts" is base filename
jdbc.url =  jdbc:h2:/temp/h2/contacts
# Create database tables at startup? Does nothing if tables already exist.
createtables = true
```
4. Build the source code.  To build using an IDE, add the JAR files in the "lib" directory to the project buildpath.  To build from the command line do:
```shell
# This example uses a Bash shell.  You can use the git-bash shell
cmd>  cd contacts-lite
cmd>  mkdir bin
cmd>  cd src
cmd>  javac -cp '../lib/*' -d ../bin *.java util/*.java
```
5. Run the main class `ContactsApp`.


## Libraries Used

The JARs for ORMLite and H2 database are in the project `lib` directory.
They must be on the Java classpath to compile and run the application.

* lib/ormlite-core-5.1.jar
* lib/ormlite-jdbc-5.1.jar
* lib/h2-2.1.197.jar - JDBC driver and related files for H2 database

## What the Application Shows

`Contact.java` is the class for objects we want to persist (called "entitites"). This class has ORMLite annotations to tell ORMLite where to save the objects.

```java
@DatabaseTable(tableName="contacts")
public class Contact {
    // The "id" will be the primary key for the database table
    // generatedId=true means the database will assign an id value itself
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
        // the id is assigned by the database, so don't set it here.
        this.name = name;
        this.telephone = telephone;
        this.email = email;
     }

     ... other methods, such as getName(), setName(), equals()
}
```

In `Contact` the ORMLite annotations are used, but you can use standard JPA annotations instead (see ORMLite User's Guide, Chapter 2). `@DatabaseField` indicates an attribute that should be saved in the database.  The annotation has many options. For example, to save the contact's name as a field of width 80 you can write:
```java
    @DatabaseField(width=80)
    private String name;
```
Setting the width is useful if you want ORMLite to create the table schema for you.

ORMLite creates a *Data Access Object* for persisting your entity classes.
To create a DAO for "Contact" objects, we write:
```java
ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL);
Dao<Contact,Long> contactDao =
         DaoManager.createDao(connectionSource, Contact.class);
```

You first create a connection to the database,
then use it to instantiate a DAO for your entity class(es) using `DaoManager`.
The two type parameters `Dao<Type,Key>` are the entity class name and the type of the id field (table primary key).  In `Contact` we used a `Long` as the id field.  If you use a primitive, such as "int" for the id field, write `int.class` for the Key type parameter.

In a real application you probably want to write your own DAO so that you can add custom search methods.  With ORMLite you can write a DAO that extends `BaseDaoImpl`, and add any methods you like.  BaseDaoImpl provides the basic `create()`, `delete()`, update()`, and query methods, so you only need to write your custom code.

You can also use a PooledConnectionSource for many connections if you need them.

### What the DAO Does: Persistence Operations

The Dao objects (like `contactDao`) provide basic database operations

| contactDao Method | What is does                 |
|:------------------|:-----------------------------|
| create(contact) | save a contact object to the database |
| update(contact) | update record for existing contact with values from contact object |
| queryForId(id)  | find object having the given id.  |
| queryForEq(fieldName,value)  | find objects having a field with given value  |
| iterator()      | get all contacts from the database, as an Iterator |
| delete(contact) | delete a contact from the database |
| countOf()       | return the number of rows in the table for this Dao |

The basic operations Create, Retrieve, Update, and Delete form the acronym CRUD, a common term in software design.

To save a new contact to the database, simply write:

```java
Contact contact = new Contact("Bill Gates","14145550001","bill@microsoft.com");
contactDao.create( contact );
// the database assigns an id to the object:
System.out.println("Bill saved with id "+contact.getId());

// Find a contact named "Bill Gates"
Contact result = contactDao.queryForEq("name","Bill Gates");
```

Finding or querying objects in the database is the most complex operation, since there many ways you might want to "search" for something.  The DAO has many query methods which work for most simple queries.

For more general queries, ORMLite provides a `QueryBuilder` class.
This is better than using raw SQL, like "SELECT * from contacts WHERE ...", which is error-prone and vulnerable to SQL Injection attacks.

If we want to find all contacts that use Gmail we could write:
```java
QueryBuilder<Contact,Long> qb = contactDao.queryBuilder();
// This is like SQL "WHERE email LIKE '%@gmail.com'
qb = qb.where().like("email","%@gmail.com");
// perform the query. Results are returned as a List.
List<Contact> results = qb.query();

System.out.printf("Found %d gmail users\n", results.size());
for(Contact c: results) System.out.println( c.getName() );
```


## How To Limit Log Messages?

ORMLite has a built-in Logging facility named LocalLog.  It generates a lot of output messages.  To set the minimum severity of log messages, see:
[http://ormlite.com/javadoc/ormlite-core/com/j256/ormlite/logger/LocalLog.html](http://ormlite.com/javadoc/ormlite-core/com/j256/ormlite/logger/LocalLog.html)

In the example code there is a static block to specify that we only want to see messages of severity "error" or higher.
```java
static {
    System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "error");
}
```

## Why Use a Properties File?

ContactsApp needs the database URL to create a database connection. It also uses a boolean flag `CREATE_TABLES` to indicate whether it should try to create the database schema.   We could set these in Java code like this:
```java
// Name of a directory and base name for files created in that directory.
private static final String DATABASE_URL = "jdbc:h2:/home/jim/h2/contacts";
// Try to create database tables at startup? (Does nothing if tables already exist.)
private static final boolean CREATE_TABLES = true;
```
but it is **bad coding style** to hard-code configuration information in code.

Instead, we put this information in a properties file (`contacts.config`) in the format:
```
# The database URL.
# For H2 it should be a directory and the basename of the files that
# H2 will use for your database.
jdbc.url = jdbc:h2:/temp/h2/contacts
# Create database tables if they don't exist?
createtables = true
```

The class `util.PropertyManager` reads this configuration file and creates a Java Properties object containing key-value pairs read from the file.
PropertyManager searches for the configuration file on the application classpath.  By putting `contacts.config` in the src directory, it will be copied to the `bin` directory during project build.

To get a property value in Java code, we write:

```java
final String DATABASE_URL = PropertyManager.getProperty("jdbc.url");
```

PropertyManager also has a `getProperties()` method that returns all the properties as a Properties object.  You can use this for testing, to print all the values on the console.
```java
java.util.Properties properties = PropertyManager.getProperties();
properties.list( System.out );
```

The property names can be anything you like, including "." in the name. 
The standard standard names for JDBC properties are:

```
# The URL for your database
jdbc.url = jdbc:h2:/home/jim/h2/contacts
# The JDBC Driver file. This usually isn't needed.
jdbc.driver = org.h2.Driver
# The database user name and password, needed for a client-server database.
jdbc.user =
jdbc.password =
```

## References

* [ORMLite.com][ORMLite] home for ORMLite software and documentation.
* [ORMLite User Guide](http://ormlite.com/docs/ormlite.pdf).
* [H2 Database][H2] the H2 embedded database.
* [Intro to Database](https://skeoop.github.io/database/) slides introducing database and ORM concepts.

---

[ORMLite]: http://ormlite.com
[H2]: http://www.h2database.com
