package edu.ucla.loni.ccb.itools.dao.db;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;

import edu.ucla.loni.ccb.itools.dao.UserDao;
import edu.ucla.loni.ccb.itools.model.RUser;

public class DatabaseUserDao implements UserDao {

	public RUser getUser(String user) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List list = session.createQuery("from RUser where name=?").setString(0, user).list();
		session.close();

		return (list.size() == 0) ? null : (RUser) list.get(0);
	}

	public String[] getAllUserNames() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Collection rv = session.createQuery("select name from RUser").list();
		session.close();
		return (String[]) rv.toArray(new String[0]);
	}

	public void addUser(RUser ruser) {
		HibernateUtil.runInTransaction(HibernateUtil.ADD, ruser);
	}

	public void updateUser(RUser ruser) {
		HibernateUtil.runInTransaction(HibernateUtil.UPDATE, ruser);
	}
}
