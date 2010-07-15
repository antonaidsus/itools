package edu.ucla.loni.ccb.itools.dao.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import edu.ucla.loni.ccb.itools.dao.DaoHelper;
import edu.ucla.loni.ccb.itools.dao.ResourceDao;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

public class DatabaseResourceDao implements ResourceDao {
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(DatabaseResourceDao.class);
	
	public List<NcbcResource> load(String center) {
		return runQuery("from NcbcResource where center='"+ center +"'");
	}

	public List<NcbcResource> getAllResources() {
		return runQuery("from NcbcResource");
	}

	@SuppressWarnings("unchecked")
	private List<NcbcResource> runQuery(String string) {
		Session session = null;
		List<NcbcResource> rv = new ArrayList<NcbcResource>(0);
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			rv = session.createQuery(string).list();
		} catch (Exception e) {
			LOGGER.warn(e,e);
		} finally {
		    if (session != null) session.close();
		}
		return rv;
	}

	public void removeResource(NcbcResource rsc) {
		HibernateUtil.runInTransaction(HibernateUtil.DELETE, rsc);
	}

	public void addResource(NcbcResource rsc) {
		LOGGER.debug("before: " + rsc.getId());
		HibernateUtil.runInTransaction(HibernateUtil.ADD, rsc);
		LOGGER.debug("after: " + rsc.getId());

	}

	public void updateResource(NcbcResource rsc) {
		LOGGER.debug("before: " + rsc.getId());
		HibernateUtil.runInTransaction(HibernateUtil.UPDATE, rsc);
		LOGGER.debug("after: " + rsc.getId());
	}

	public List<NcbcResource> getResources(String reg, String category) {
		return DaoHelper.searchResources(reg,category, getAllResources());
	}

	public List<NcbcResource> getResources(String reg, String category, String center) {
		return DaoHelper.searchResources(reg,category, load(center));
	}

	public void setResources(List<NcbcResource> rscs) {
		if (rscs.size() == 0) {
			return;
		}
		NcbcResource object = rscs.get(0);
		String center = object.getCenter();
		String hql ="delete from NcbcResource where center=" + center;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.createQuery(hql).executeUpdate();
			for (Iterator<NcbcResource> iter = rscs.iterator(); iter.hasNext();) {
				NcbcResource element = (NcbcResource) iter.next();
				session.save(element);
			}
			session.flush();
			tx.commit();
			HibernateUtil.flushDb(session);
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				LOGGER.error(e,e);
			}
		} finally {
			session.close();
		}
	}
	public boolean canRecover(String center) {
		return false;
	}
	public List<NcbcResource> recover(String center) {
		return new ArrayList<NcbcResource>(0);
	}

	public String getOntologyString() {
		throw new RuntimeException("Not implemented yet for DB");
	}

	public void setOntologyString(String ontology) {
		throw new RuntimeException("Not implemented yet for DB");
	}

}
