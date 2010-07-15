package edu.ucla.loni.ccb.itools.dao.remote;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Properties;

import edu.ucla.loni.ccb.itools.dao.ReviewDao;
import edu.ucla.loni.ccb.itools.model.ResourceReview;
import edu.ucla.loni.ccb.itools.parser.ReviewParser;

public class AppletReviewDao implements ReviewDao {

	public List<ResourceReview> getResourceReviews(String resourceName) {
		String str = ProxyServer.talk2Server("review.htm?method=getReviews&resourceName=" + resourceName);
		return ReviewParser.parse(new ByteArrayInputStream(str.getBytes()));
	}

	public void add(ResourceReview value) {
		String str = ReviewParser.getSaveString(value).trim();
		Properties  props = new Properties();
		props.put("method", "addReview");
		props.put("review", str);
		ProxyServer.talk2HttpsServer("restrictedReview.htm", props);
	}

	public void update(ResourceReview value) {
		String str = ReviewParser.getSaveString(value).trim();
		Properties  props = new Properties();
		props.put("method", "updateReview");
		props.put("review", str);

		ProxyServer.talk2Server("review.htm?method=updateReview", props);
	}
}
