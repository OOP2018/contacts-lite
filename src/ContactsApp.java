import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import util.PropertyManager;

/**
 * Example of using ORMlite for persisting data about
 * a single kind of object (Contact).
 * Requires JARs for H2 (or database of your choice) and ORMlite.
 */
public class ContactsApp {
	public static final Scanner console = new Scanner(System.in);
	// The name of a directory + base name of database files.
	// The directory must already exist, but the files can be created
	//private static final String DATABASE_URL = "jdbc:h2:/home/jim/temp/h2/contacts";
	static final String DATABASE_URL = PropertyManager.getProperty("jdbc.url");

	// Create database tables at startup? (Does nothing if tables exist.)
	private static final boolean CREATE_TABLES = 
			Boolean.valueOf( PropertyManager.getProperty("createtables") );

	// The Data Access Object (DAO) for Contacts objects
	private static Dao<Contact,Long> contactDao;

    // This code is to limit which ORLLite log messages are printed on console.
	static {
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "error");
    }
	
	public static void main(String[] args) throws SQLException, IOException {
		// Create ORMLite connection to the database. You only need one
		ConnectionSource connSource = new JdbcConnectionSource(DATABASE_URL);
		
		// Create database tables. Only the first time, or if you change the schema.
		if (CREATE_TABLES) createTables(connSource);
		
		// Create a data access object (DAO) for Contact objects
		contactDao = DaoManager.createDao(connSource, Contact.class);
		
		// Add some initial data to the database? (uses a CSV file)
		if (contactDao.countOf() == 0) addContacts("data/students.csv");
		
		// Query and display contacts.
		queryContacts();
		
		//printAllContacts();
		connSource.close();
	}
	
	/**
	 * Add some contacts to the database using data from a CSV file.
	 * Each line of the CSV file containss name,telephone,email for one contact.
	 * If a contact with matching name is already in the database,
	 * it is not added again.
	 * @param filename name of a CSV-format file containing the 
	 *    contacts name, telephone, email, one contact per line.
	 */
	public static void addContacts(String filename) {
		System.out.println("Adding contacts data from "+filename);
		
		ClassLoader loader = ContactsApp.class.getClassLoader();
		InputStream in = loader.getResourceAsStream(filename);
		if (in == null) {
			System.out.println("Could not find data file "+filename);
			System.out.println("Is it on the classpath?");
			return;
		}
		Scanner reader = new Scanner(in);
		try {
		
			while(reader.hasNextLine()) {
				String line = reader.nextLine().trim();
				if (line.isEmpty() || line.startsWith("#")) continue; // skip comment lines
				String[] fields = line.split("\\s*,\\s*");
				if (findByName(fields[0]) != null) continue; // already in database
				// add a new contact to the database table for Contacts
				Contact c = new Contact(fields[0],fields[1], fields[2]);
				contactDao.create( c );
			}
		} 
		catch( SQLException ex ) {
			System.out.println("addContacts threw SQLException: "+ex.getMessage());
		}
		finally {
			reader.close();
		}
	}
	
	/**
	 * Query contacts by name.
	 * This shows how to query something in the database
	 * using a QueryBuilder.
	 */
	public static void queryContacts() {
		System.out.println("Input name of contact to find or % to print all contacts.");
		System.out.println("Enter 'quit' to quit.");
		while(true) {
			System.out.print("Name of contact? ");
			String name = console.nextLine().trim();
			if (name.equalsIgnoreCase("quit")) return;
			// Use QueryBuilder to build a query.
			// You can append many clauses onto this to "build" your query.
			QueryBuilder<Contact,Long> qb = contactDao.queryBuilder();
			
			try {
				// "where" has MANY methods, for example:
				// where().eq(columnName, value)
				// where().ge(columnName, value)  uses "columnName >= value"
				// where().like(columnName, value) uses "columnName LIKE value"
				// in SQL "%" is a wildcard character (matches anything)
				// name LIKE "Foo%"  matches any name that starts with "Foo".
				qb.where().like("name", name+"%");
				// Perform the query.  May throw SQLException.
				List<Contact> results = qb.query();
				System.out.printf("Found %d matches.\n", results.size() );
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
	
	/**
	 * Find a contact by name.
	 * In a real application, this method would be part of your ContactDao
	 * instead of in this class.
	 * @param name is the contact's name
	 * @return the *first* matching contact, with exact match of the name.
	 *    Returns null if no match.
	 */
	public static Contact findByName(String name) {
		QueryBuilder qb = contactDao.queryBuilder();
		try {
			Contact match = (Contact) qb.where().eq("name", name).queryForFirst();
			return match;
		} catch (SQLException sqle) {
			//TODO
			return null;
		}
	}
	
	public static void printAllContacts() throws SQLException {
		// ContactDao implements Iterable.
		// It creates an Iterator for all the Contacts in the database.
		// So, you can use it in a for-each loop or call the forEach() method.
	
		System.out.printf("Number of contacts: %d\n", contactDao.countOf());
		Iterator<Contact> it = contactDao.iterator();
		while( it.hasNext() ) {
			Contact c = it.next();
			System.out.printf("%s <%s>\n", c.getName(), c.getEmail());
		}
		//or:
		//contactDao.forEach( c -> System.out.println(c.getName()+" "+c.getEmail()) );	
	}

	/**
	 * Create database tables (schema) from Java classes.
	 * You really only need to do this one time.
	 * @param connSource the ORMLite connectionSource for the database
	 */
	private static void createTables(ConnectionSource connSource)  {
		// With HSQLDB this throws exception about ID generator sequence
		// if a table already exists.
		try {
			// Create a table for each entity class.
			TableUtils.createTableIfNotExists(connSource, Contact.class);
		} 
		catch (java.sql.SQLException ex) {
			System.out.println(ex.getMessage());
			// You need to be careful here.
			// If the database or schema doesn't exist your app will fail later.
		}
	}
}
