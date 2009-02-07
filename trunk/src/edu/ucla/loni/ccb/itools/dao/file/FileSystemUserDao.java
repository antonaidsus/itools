package edu.ucla.loni.ccb.itools.dao.file;

import edu.ucla.loni.ccb.itools.dao.UserDao;
import edu.ucla.loni.ccb.itools.model.RUser;

public class FileSystemUserDao implements UserDao {

	public RUser getUser(String user) {
		return AccountIO.getUser(user);
	}

	public String[] getAllUserNames() {
		return AccountIO.getAllUsers();
	}

	public void addUser(RUser ruser) {
		AccountIO.save(ruser);
	}

	public void updateUser(RUser ruser) {
		AccountIO.save(ruser);
	}
}
