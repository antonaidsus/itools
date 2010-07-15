package edu.ucla.loni.ccb.itools.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.ResourceReview;

/**
 * This class handles user's requests for review, but need at least of Role of
 * Normal User.
 * 
 * @author Jeff Qunfei Ma
 *
 */
public class RestrictedReviewController extends ReviewController {
	/* (non-Javadoc)
	 * @see edu.ucla.loni.ccb.itools.servlet.ReviewController#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String v = req.getParameter("method");
		String message = req.getRequestURI();
		LOGGER.debug("Restricted:" + message);

		if (v == null) {
			LOGGER.warn("secure: No method parameter, don't know what to do.");
		} else if (v.equals("addReview")) {
			ResourceReview review = string2Review(req);
			if (review != null) {
				DaoFactory.getDaoFactory().getReviewDao().add(review);
			}
		} else {
			return super.handleRequest(req, res);
		}
		return null; //new ModelAndView("daoservice","message", message);
	}	
}
