package util;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 * Manage properties for this application.
 * Uses static behavior and a private Properties object to manage properties.
 * To specify a properties file, the first call should be setPropertiesFile(FILENAME)
 */
public class PropertyManager {
	// What's the name of properties file?
	private static final String properties_filename = "contacts.config";
	
	/** This static block defines a custom format for Logger messages. 
	 *  I don't like the default 2-line format, so this defines a short 1-line format.  
	 *  This static block should go in the Main class so its executed once before anything else.
	 */
    static {
        // %1=datetime %3=loggername %4=level %5=message
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT %3$s %4$-7s %5$s%n");
    }
    
	// java.util.Properties object that will hold all properties for the app.
    // property names/values are loaded from a file.
	private static Properties properties = null;
	// Print log messages using java.util.logging.Logger
	private static final Logger logger = Logger.getLogger(PropertyManager.class.getSimpleName());	

	
	/** 
	 * Private constructor to prevent other objects from creating an instance.
	 * Use static methods to access properties.
	 */
	private PropertyManager() {
		// You could call loadProperties() here to force early failure.
	}

	/**
	 * Read properties from a file.  The property filename can be specified
	 * in either Config.java or at system level using java -Dproperties=/path/filename
	 * on by setting an environment variable.
	 */
	private static void loadProperties( ) {
		if ( properties != null ) return; // already loaded

		logger.info("loading properties from "+properties_filename);
		
		InputStream instream = null;
		properties = new Properties();
		ClassLoader loader = PropertyManager.class.getClassLoader();
		try {
			instream = loader.getResourceAsStream( properties_filename );
			properties.load( instream );
		} catch (java.io.IOException ex) {
			logger.log(Level.SEVERE, "couldn't load properties from " + properties_filename, ex );
		} finally {
			if ( instream != null ) try { instream.close(); } catch(Exception e) {/* ignore it */}
		}

	}
	
	/** 
	 *  Get a property value using a String name.
	 *  The name should be a property name in the application's properties file.
	 *  @param property name (key) of the property to get
	 *  @return current value of the property
	 */
	public static String getProperty( String property ) {
		if ( properties == null )  loadProperties();
		return properties.getProperty( property, "" /*default value*/ );
	}
	
	/**
	 * Set the value of a property.  The value overrides the existing value,
	 * but only for duration of this process (new value not saved to a file).
	 * 
	 * @param property is the Property member to set
	 * @param newvalue is the new value for this property
	 */
	public static void putProperty( String property, String newvalue ) {
		if ( properties == null )  loadProperties();
		properties.setProperty( property, newvalue );
	}

	/** save the values of Properties to a file as plain text 'key=value'
	 * @param filename is the name of the file to write properties to.
	 */
	public static void saveProperties( String filename ) {
		if ( properties == null )  loadProperties();
		try {
			java.io.FileOutputStream fout = new java.io.FileOutputStream( filename );
			// add a comment line to properties file
			properties.store(fout, 
					"properties saved on "+ java.util.Calendar.getInstance().toString() );
			fout.close();
		} catch ( java.io.IOException ex ) { 
			logger.log(Level.SEVERE, "error saving properties to " + filename, ex );
		}
	}
	
	/** 
	 * Return a reference to the Properties object.
	 * Any changes to the properties will affect values returned later,
	 * so caller should be careful not to change the properties unless
	 * its deliberate.
	 * 
	 * @return the current properties
	 */
	public static Properties getProperties() {
		if ( properties == null ) loadProperties();
		return properties;
	}
	
	/**
	 * For testing use of PropertyManager.
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Getting all properties from PropertyManager\n");
		Properties props = PropertyManager.getProperties();
		/* print them all */
		for( Object obj : props.keySet() ) {
			String key = (String)obj;
			System.out.printf("%s=%s\n", key, properties.getProperty(key) );
		}
	}

}
