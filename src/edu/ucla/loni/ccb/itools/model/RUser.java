/**
 * 
 */
package edu.ucla.loni.ccb.itools.model;

/**
 * @author Jeff Qunfei Ma
 * @hibernate.class
 */
public class RUser {
	String name;

	String passwd;

	String email;

	String role = NORMALUSER;

	public static final String NOTTHING = "ROLE_NOTHING";

	public static final String NORMALUSER = "ROLE_NORMALUSER";

	public static final String EXPERTUSER = "ROLE_EXPERTUSER";

	public static final String ADMINISTRATOR = "ROLE_ADMINISTRATOR";

	public RUser(String user2, String pswd, String email2) {
		name = user2;
		passwd = pswd;
		email = email2;
	}

	public RUser() {
	}

	private int id; // required by Hibernate.

	/**
	 * @hibernate.id column="id" generator-class="native" unsaved-value="null"
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getRole() {
		return role;
	}

	public void setRole(String str) {
		role = str;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String user) {
		this.name = user;
	}

	public String toXML(String idt) {
		return idt + "<user name=\"" + name + "\" id=\"" + id + "\" role=\"" + role 
				+ "\" email=\"" + email + "\" password=\"" + passwd + "\"/>\n";
	}
	
	public boolean isExpertUser() {
		return EXPERTUSER.equals(role);
	}
	
	public boolean isAdministrator() {
		return ADMINISTRATOR.equals(role);
	}
	
	public boolean isNormalUser() {
		return NORMALUSER.equals(role);
	}
}