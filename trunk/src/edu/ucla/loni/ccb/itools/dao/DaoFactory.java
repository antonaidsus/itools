package edu.ucla.loni.ccb.itools.dao;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.dao.db.DatabaseDaoFactory;
import edu.ucla.loni.ccb.itools.dao.file.FileSystemDaoFactory;
import edu.ucla.loni.ccb.itools.dao.remote.AppletDaoFactory;

public abstract class DaoFactory {
	/**
	 * Logger for this class
	 */
	public static final Logger LOGGER = Logger.getLogger(DaoFactory.class);
	
	public static final int FILESYSTEM = 1;
	public static final int DATABASE = 2;
	public static final int APPLET = 3;
	public static final int MOCK = 4;
	
	
	public abstract ResourceDao initResourceDao();
	public abstract ReviewDao initReviewDao();
	public abstract UserDao initUserDao();
	public abstract WebSearchHistoryDao initWebSearchHistoryDao();
	public abstract CenterDao initCenterDao();
	
	private static FileSystemDaoFactory filesystemInst;
	private static AppletDaoFactory appletInst;
	private static DatabaseDaoFactory databaseInst;
	
	private ResourceDao resourceDao;
	private ReviewDao reviewDao;
	private UserDao userDao;
	private WebSearchHistoryDao webSearchHistoryDao;
	private CenterDao centerDao;

	public final ResourceDao getResourceDao() {
		if (resourceDao == null) {
			resourceDao = initResourceDao();
		}
		return resourceDao;
	}
	public final ReviewDao getReviewDao() {
		if (reviewDao == null) {
			reviewDao = initReviewDao();
		}
		return reviewDao;
	}
	public UserDao getUserDao() {
		if (userDao == null) {
			userDao = initUserDao();
		}
		return userDao;
	}
	
	public WebSearchHistoryDao getWebSearchHistoryDao() {
		if (webSearchHistoryDao == null) {
			webSearchHistoryDao = initWebSearchHistoryDao();
		}
		return webSearchHistoryDao;
	}

	public CenterDao getCenterDao() {
		if (centerDao == null) {
			centerDao = initCenterDao();
		}
		return centerDao;
	}

	public static DaoFactory  getDaoFactory(int factory) {
		switch(factory) {
		case FILESYSTEM :
			return getFilesystemInst();
		case DATABASE :
			return getDatabaseInst();
		case APPLET :
			return getAppletInst();
	    default :
	    	return getFilesystemInst();
		}
	}
	
	public static DaoFactory  getDaoFactory() {
		if (Main.getProperty("server.url") != null) {
			return getAppletInst();
		}
		
		String daotype = Main.getProperty("dao.type");
		if (daotype == null) {
			return getFilesystemInst();
		} else if (daotype.equalsIgnoreCase("applet")) {
			return getAppletInst();
		} else if (daotype.equalsIgnoreCase("hibernate")) {
			return getDatabaseInst();
		} else if (daotype.equalsIgnoreCase("jdbc")) {
			return getDatabaseInst();
		} else if (daotype.equalsIgnoreCase("filesystem")) {
			return getFilesystemInst();
		} else {
			return getFilesystemInst();
		}
		
//		if (Main.isServerMode()) {
//			return getFilesystemInst();
//		}
//		if (Main.isAppletMode() || Main.getServerUrl() != null) {
//			return getAppletInst();
//		} else if (){
//		    return getFilesystemInst();
//		} else {
//		    return getFilesystemInst();
//		}
	}

	private static FileSystemDaoFactory getFilesystemInst() {
		if (filesystemInst == null) {
			filesystemInst = new FileSystemDaoFactory();
		}
		return filesystemInst;
	}
	
	private static AppletDaoFactory getAppletInst() {
		if (appletInst == null) {
			appletInst = new AppletDaoFactory();
		}
		return appletInst;
	}
	
	private static DatabaseDaoFactory getDatabaseInst() {
		if (databaseInst == null) {
			databaseInst = new DatabaseDaoFactory();
		}
		return databaseInst;
	}
}
