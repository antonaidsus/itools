package edu.ucla.loni.ccb.itools.dao.file;

import edu.ucla.loni.ccb.itools.dao.CenterDao;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.dao.ResourceDao;
import edu.ucla.loni.ccb.itools.dao.ReviewDao;
import edu.ucla.loni.ccb.itools.dao.UserDao;
import edu.ucla.loni.ccb.itools.dao.WebSearchHistoryDao;

public class FileSystemDaoFactory extends DaoFactory {
	public ResourceDao initResourceDao() {
		return new FileSystemResourceDao();
	}

	public ReviewDao initReviewDao() {
		return new FileSystemReviewDao();
	}

	public UserDao initUserDao() {
		return new FileSystemUserDao();
	}
	
	public WebSearchHistoryDao initWebSearchHistoryDao() {
		return new FileSystemWebSearchHistoryDao();
	}

	public CenterDao initCenterDao() {
		return new FileSystemCenterDao();
	}
}
