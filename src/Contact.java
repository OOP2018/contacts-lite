import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A class for objects to save in a database table.
 * Each object of this class should have unique identity
 * field that is used as the "primary key" in the database.
 * In this example we let the database generate and assign
 * the id itself.  When an object is saved to the database
 * itd id will be updated.
 */
@DatabaseTable(tableName="contacts")
public class Contact {
    // The id field
	@DatabaseField(generatedId=true)
	private Long id;
	@DatabaseField
	private String name;
	@DatabaseField
	private String telephone;
	@DatabaseField
	private String email;
	
	public Contact() {
		//ORMLite requires a no-arg constructor
	}

	/**
	 * @param name
	 * @param telephone
	 * @param email
	 */
	public Contact(String name, String telephone, String email) {
		super();
		this.id = null;
		this.name = name;
		this.telephone = telephone;
		this.email = email;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param telephone the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
