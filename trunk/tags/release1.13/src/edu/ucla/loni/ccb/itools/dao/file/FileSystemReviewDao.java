package edu.ucla.loni.ccb.itools.dao.file;

import java.util.List;

import edu.ucla.loni.ccb.itools.dao.ReviewDao;
import edu.ucla.loni.ccb.itools.model.ResourceReview;

public class FileSystemReviewDao implements ReviewDao {

	public void add(ResourceReview value) {
		ResourceReviewIO.add(value);
	}
	
	public void update(ResourceReview value) {
		ResourceReviewIO.update(value);
	}

	public List<ResourceReview> getResourceReviews(String resourceName) {
		return ResourceReviewIO.getReviews(resourceName);
	}
}
