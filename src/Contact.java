import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="contacts")
public class Contact {
	// This is for HSQL which uses a sequence named "TABLENAME_ID_SEQ", ie. "CONTACTS_ID_SEQ"
	// For other databases, try "(generatedId=true)" instead. See ORMLite Section 2.8.2.
	//@DatabaseField(generatedIdSequence="CONTACTS_ID_SEQ")
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
