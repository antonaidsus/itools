package edu.ucla.loni.ccb.itools.dao.remote;

import edu.ucla.loni.ccb.itools.dao.UserDao;
import edu.ucla.loni.ccb.itools.model.RUser;

/**
 * 
 * Responsible for acess User when run as Applet.
 * It seemed runtime will not and should not need invoke this class.
 * @author Jeff Qunfei Ma
 *
 */
public class AppletUserDao implements UserDao {

	public RUser getUser(String user) {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveUser(RUser ruser) {
		// TODO Auto-generated method stub

	}

	public String[] getAllUserNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addUser(RUser ruser) {
		// TODO Auto-generated method stub
		
	}

	public void updateUser(RUser ruser) {
		// TODO Auto-generated method stub
		
	}

}
