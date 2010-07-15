package edu.ucla.loni.ccb.itools.dao.remote;

import edu.ucla.loni.ccb.itools.dao.CenterDao;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.dao.ResourceDao;
import edu.ucla.loni.ccb.itools.dao.ReviewDao;
import edu.ucla.loni.ccb.itools.dao.UserDao;
import edu.ucla.loni.ccb.itools.dao.WebSearchHistoryDao;

public class AppletDaoFactory extends DaoFactory {
	public ResourceDao initResourceDao() {
		return new AppletResourceDao();
	}

	public ReviewDao initReviewDao() {
		return new AppletReviewDao();
	}

	public UserDao initUserDao() {
		return new AppletUserDao();
	}

	public WebSearchHistoryDao initWebSearchHistoryDao() {
		return new AppletWebSearchHistoryDao();
	}

	public CenterDao initCenterDao() {
		return new AppletCenterDao();
	}
}
