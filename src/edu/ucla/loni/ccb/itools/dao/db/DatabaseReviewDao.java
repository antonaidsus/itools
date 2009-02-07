package edu.ucla.loni.ccb.itools.dao.db;

import java.util.List;

import org.hibernate.Session;

import edu.ucla.loni.ccb.itools.dao.ReviewDao;
import edu.ucla.loni.ccb.itools.model.ResourceReview;

public class DatabaseReviewDao implements ReviewDao {
	public void add(ResourceReview value) {
		HibernateUtil.runInTransaction(HibernateUtil.ADD, value);
	}

	@SuppressWarnings("unchecked")
	public List<ResourceReview> getResourceReviews(String resourceName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<ResourceReview> rv = session.createQuery("From ResourceReview where resourceName=?").setString(0, resourceName).list();
		session.close();
		return rv;
	}

	public void update(ResourceReview value) {
		HibernateUtil.runInTransaction(HibernateUtil.UPDATE, value);
	}	
}
