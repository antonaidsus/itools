package edu.ucla.loni.ccb.itools.dao.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.dao.ResourceDao;
import edu.ucla.loni.ccb.itools.dao.ReviewDao;
import edu.ucla.loni.ccb.itools.dao.UserDao;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.model.RUser;
import edu.ucla.loni.ccb.itools.model.ResourceReview;
import edu.ucla.loni.ccb.itools.parser.CentersParser;

/**
 * 
 * This class used to populate database from FileSystem.
 * @author Jeff Qunfei Ma
 *
 */
public class DatabasePopulator {
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger
			.getLogger(DatabasePopulator.class);
	
    /**
     * transfer all data from file system to database system.
     */
    public static void file2Database() {
    	List<NcbcCenter> centers = DaoFactory.getDaoFactory().getCenterDao().getCenters();
		ResourceDao resourceDao = DaoFactory.getDaoFactory(DaoFactory.FILESYSTEM).getResourceDao();
		ReviewDao reviewDao = DaoFactory.getDaoFactory(DaoFactory.FILESYSTEM).getReviewDao();
		UserDao userDao = DaoFactory.getDaoFactory(DaoFactory.FILESYSTEM).getUserDao();

		for (Iterator iter = centers.iterator(); iter.hasNext();) {
			NcbcCenter element = (NcbcCenter) iter.next();
			resourceDao.load(element.getName());
		}
		
		Collection rscs = resourceDao.getAllResources();
		LOGGER.info(rscs.size() + " resources were loaded from filesystem");
		
		String[] allUserNames = userDao.getAllUserNames();
		LOGGER.info(allUserNames + " users were loaded from filesystem");
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			for (Iterator i = rscs.iterator(); i.hasNext();) {
				NcbcResource rsc = (NcbcResource) i.next();
				session.save(rsc);
				Collection resourceReviews = reviewDao.getResourceReviews(rsc.getName());
				for (Iterator iter = resourceReviews.iterator(); iter.hasNext();) {
					ResourceReview element = (ResourceReview) iter.next();
					session.save(element);
				}
			}
			
			for (int i = 0; i < allUserNames.length; i++) {
				session.save(userDao.getUser(allUserNames[i]));
			}
			
			session.flush();

//			Collection rv = session.createQuery("from NcbcResource").list();
//			logger.info(rv.size() + " resources were loaded from DB for center CCB");
			tx.commit();
			HibernateUtil.flushDb(session);
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			LOGGER.error(e,e);
		} finally {
			session.close();
		}
    }
    
    /**
     * Transfer all data from database to file system.
     */
    public static void database2file() {
    	LOGGER.debug("database 2 file");
    	ResourceDao resourceDao = DaoFactory.getDaoFactory(DaoFactory.DATABASE).getResourceDao();
		ReviewDao reviewDao = DaoFactory.getDaoFactory(DaoFactory.DATABASE).getReviewDao();
		UserDao userDao = DaoFactory.getDaoFactory(DaoFactory.DATABASE).getUserDao();		
		
		ResourceDao resourceDao2 = DaoFactory.getDaoFactory(DaoFactory.FILESYSTEM).getResourceDao();
		ReviewDao reviewDao2 = DaoFactory.getDaoFactory(DaoFactory.FILESYSTEM).getReviewDao();
		UserDao userDao2= DaoFactory.getDaoFactory(DaoFactory.FILESYSTEM).getUserDao();
        //resources
		List<NcbcResource> allResources = resourceDao.getAllResources();
		LOGGER.debug(allResources.size() + " resouces will move from database to filesystem");
		resourceDao2.setResources(allResources);
		// and reviews
		for (Iterator iter = allResources.iterator(); iter.hasNext();) {
			NcbcResource rsc = (NcbcResource) iter.next();
			Collection resourceReviews = reviewDao.getResourceReviews(rsc.getName());
			for (Iterator iterator = resourceReviews.iterator(); iterator
					.hasNext();) {
				ResourceReview review = (ResourceReview) iterator.next();
				reviewDao2.add(review);				
			}			
		}
	    //users
		String[] allUserNames = userDao.getAllUserNames();
		for (int i = 0; i < allUserNames.length; i++) {
			RUser user = userDao.getUser(allUserNames[i]);
			userDao2.addUser(user);
		}
    	
    }
    
    public static void main(String[] args) {
		if (args.length < 1) {
			usage();
			return;
		}
		LOGGER.info(args[0] + " started");
		if (args[0].equals("file2database")) {
			Main.loadProperties(false);
			file2Database();
		} else if (args[0].equals("database2file")) {
			Main.setServerMode(true);
			Main.loadProperties(true);
			database2file();
		}
	}

	private static void usage() {
		System.out.println("java DatabasePopulator arg");
		System.out.println("where arg is file2database");		
	}
}
