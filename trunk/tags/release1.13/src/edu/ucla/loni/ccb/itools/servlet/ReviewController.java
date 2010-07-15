package edu.ucla.loni.ccb.itools.servlet;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.ResourceReview;
import edu.ucla.loni.ccb.itools.parser.ReviewParser;

/**
 * This controller handles the client's request for review.
 * @author Jeff Qunfei Ma
 *
 */
public class ReviewController extends IToolsController {	
	public static final Logger LOGGER = Logger.getLogger(ReviewController.class);
	
	public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String v = req.getParameter("method");
		String message = req.getRequestURI();
		LOGGER.debug(message);
		if (v == null) {
			LOGGER.warn("No method parameter, don't know what to do.");
		}  else if (v.equals("updateReview")) {
			ResourceReview review = string2Review(req);
			if (review != null) {
				DaoFactory.getDaoFactory().getReviewDao().update(review);
				message += "?sucess";
			}
		}  else if (v.equals("getReviews")) {
			String resourceName = req.getParameter("resourceName");
			List<ResourceReview> reviews = DaoFactory.getDaoFactory().getReviewDao().getResourceReviews(resourceName);
			message =ReviewParser.toXML(reviews);
			LOGGER.debug(message);
		} 
		return writeResponse(res, message);		
	}
	
	static ResourceReview string2Review(HttpServletRequest req) {
		String str = req.getParameter("review");
		List<ResourceReview> list = ReviewParser.parse(new ByteArrayInputStream(str.getBytes()));
		if (list.size() == 0) {
			LOGGER.error("no review created from:\n" + str);
			return null;
		}
		ResourceReview rv= list.get(0);
		return rv;
	}
}
