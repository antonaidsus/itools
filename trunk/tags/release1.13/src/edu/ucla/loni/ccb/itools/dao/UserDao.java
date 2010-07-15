package edu.ucla.loni.ccb.itools.dao;

import edu.ucla.loni.ccb.itools.model.RUser;

public interface UserDao {

	/**
	 * Gets the instance using the user name string.
	 * @param user
	 * @return
	 */
	RUser getUser(String user);

	/**
	 * Save the instance to the system.
	 * @param ruser
	 */
	void addUser(RUser ruser);
	
	void updateUser(RUser ruser);

	String[] getAllUserNames();

}
