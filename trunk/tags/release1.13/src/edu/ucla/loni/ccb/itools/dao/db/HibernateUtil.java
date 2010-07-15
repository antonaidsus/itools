package edu.ucla.loni.ccb.itools.dao.db;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.cfg.*;

import edu.ucla.loni.ccb.itools.Main;

public class HibernateUtil {
    public static final int ADD = 0;
    public static final int UPDATE = 1;
    public static final int DELETE = 2;
	public static final int SAVE_OR_UPDATE = 3;
    
	/**
	 * Logger for this class
	 */
	public static final Logger LOGGER = Logger.getLogger(HibernateUtil.class);


    private static final SessionFactory sessionFactory;

    static {
        try {
        	//System.setProperty("derby.system.home", System.getProperty("user.home", "."));
        	LOGGER.debug("Hibernate|Database information:");
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
	public static void runInTransaction(int operation, Object object) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.setFlushMode(FlushMode.COMMIT);
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			switch (operation) {
			case ADD : 
				session.save(object);
			    break;
			case UPDATE :
				session.update(object);
				break;
			case DELETE :
				session.delete(object);
				break;
			default:
				session.saveOrUpdate(object);
			}
			session.flush();
			tx.commit();
			flushDb(session);
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				LOGGER.error(e,e);
			}
		} finally {
			session.close();
		}
	}
	
	public static void runInTransaction(String hql) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.createQuery(hql).executeUpdate();
			session.flush();
			tx.commit();
			flushDb(session);
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				LOGGER.error(e,e);
			}
		} finally {
			session.close();
		}
	}
	
    /**
     * flush the dababase, it is needed for HSQLDB in-process mode
     */
    public static void flushDb(Session session) {
        if (Main.getProperty("SHUTDOWN") != null) { //for in-process Hsql
        	LOGGER.info("It is HSQL inprocess DB, shutdown command invoked");
        	//Session session = HibernateUtil.getSessionFactory().openSession();

        	try {
        		//session.createSQLQuery("COMMIT").executeUpdate();
        		Connection conn = session.connection();
        		Statement st = conn.createStatement();
        		// db writes out to files and performs clean shuts down
        		// otherwise there will be an unclean shutdown
        		// when program ends
        		st.execute("COMMIT");
        		conn.close();
        	}  catch (Exception e) {
        		e.printStackTrace();
        	} 
        }
    }
    
}