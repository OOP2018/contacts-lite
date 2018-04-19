import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Example of using ORMlite for persisting data about
 * a single kind of object (Contact).
 * Requires JARs for H2 (or database of your choice) and ORMlite.
 */
public class ContactsApp {
	public static final Scanner console = new Scanner(System.in);
	// The name of a directory + base name of database files.
	// The directory must already exist, but the files are created
    // For example: h2/contacts.mv.db h2/contacts.trace.db
	private static final String DATABASE_URL = "jdbc:h2:/home/jim/temp/h2/contacts";
	// Try to create database tables at startup? (Does nothing if tables exist.)
	private static final boolean CREATE_TABLES = true;

	// The Data Access Object (DAO) for Contacts objects
	private static Dao<Contact,Long> contactDao;
	
	public static void main(String[] args) throws SQLException, IOException {
		// Create ORMLite connection to the database. You only need one
		ConnectionSource connSource = new JdbcConnectionSource(DATABASE_URL);
		
		// Create database tables. Only the first time, or if you change the schema.
		if (CREATE_TABLES) createTables(connSource);
		
		// Create a data access object (DAO) for Contacts objects table
		contactDao = DaoManager.createDao(connSource, Contact.class);
		
		// Add some contacts -- be careful not to add same person twice
		addContacts();
		
		// Query and display contacts.
		queryContacts();
		
		connSource.close();
		
	}
	
	/**
	 * Add some contacts to the database.
	 * If the contact is already in the database, it will be added again!
	 * Call this method one time.
	 */
	public static void addContacts() {
		try {
			contactDao.create( new Contact("Jim", "0912345678", "jebrucker@gmail.com") );
			contactDao.create( new Contact("Taweerat","0862220000", "taweesoft@gmail.com") );
			contactDao.create( new Contact("Fatalaijon", "0955551212", "fatalaijon@gmail.com"));
		} catch (SQLException ex) {
			System.out.println("addContacts threw SQLException: "+ex.getMessage());
		}
		
	}
	
	/**
	 * Query contacts by name.
	 * This shows how to query something in the database.
	 */
	public static void queryContacts() {
		System.out.println("Input a blank line to quit querying.");
		while(true) {
			System.out.print("Name of contact? ");
			String name = console.nextLine().trim();
			if (name.isEmpty()) return;
			// build a query.  You can append many clauses onto this to "build" a query.
			QueryBuilder<Contact,Long> qb = contactDao.queryBuilder();
			
			try {
				// "where" has MANY methods, for example:
				// where().eq(columnName, value)
				// where().ge(columnName, value)  uses "columnName >= value"
				// where().like(columnName, value) uses "columnName LIKE value"
				// in SQL "%" is wildcard character (matches anything)
				// name LIKE "Foo%"  matches any name that starts with "Foo".
				qb.where().like("name", name+"%");
				// Perform the query.  May throw SQLException.
				List<Contact> results = qb.query();
				System.out.printf("Found %d contacts.\n", results.size() );
				// print them one per line
				results.forEach( contact -> 
				    System.out.printf("%s Tele: %s Email: <%s>\n",
				    		contact.getName(), contact.getTelephone(), contact.getEmail()) );
			}
			catch(SQLException ex) {
				System.out.println("Query threw SQLException: " + ex.getMessage());
			}
		}
	}
	
	public static void printAllContacts() throws SQLException {
		// ContactDao implements Iterable.
		// It creates an Iterator for all the Contacts in the database.
		// Since contactDao is Iterable,
		// you can use it in a for-each loop or call the forEach() method.
	
		System.out.printf("Number of contacts: %d\n", contactDao.countOf());
		contactDao.forEach( c -> System.out.println(c.getName()+" "+c.getEmail()));	
	}


	
	/**
	 * Create database tables (schema) from Java classes.
	 * You really only need to do this one time.
	 * @param connSource the ORMLite connectionSource for the database
	 */
	private static void createTables(ConnectionSource connSource)  {
		// With HSQLDB this throws exception about ID generator sequence
		// when a table already exists.
		try {
			TableUtils.createTableIfNotExists(connSource, Contact.class);
		} catch (java.sql.SQLException ex) {
			System.out.println(ex.getMessage());
			// You need to be careful here.
			// If the database or schema doesn't exist your app will fail later.
		}
	}
	
}
