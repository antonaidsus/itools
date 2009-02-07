package edu.ucla.loni.ccb.itools.dao.db;

import edu.ucla.loni.ccb.itools.dao.CenterDao;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.dao.ResourceDao;
import edu.ucla.loni.ccb.itools.dao.ReviewDao;
import edu.ucla.loni.ccb.itools.dao.UserDao;
import edu.ucla.loni.ccb.itools.dao.WebSearchHistoryDao;

public class DatabaseDaoFactory extends DaoFactory {
	public DatabaseDaoFactory() {
		
	}
	public ResourceDao initResourceDao() {		
		return new DatabaseResourceDao();
	}

	public ReviewDao initReviewDao() {
		return  new DatabaseReviewDao();
	}

	public UserDao initUserDao() {
		return new DatabaseUserDao();
	}

	public WebSearchHistoryDao initWebSearchHistoryDao() {
		// TODO Auto-generated method stub
		return null;
	}

	public CenterDao initCenterDao() {
		// TODO Auto-generated method stub
		return null;
	}
}
