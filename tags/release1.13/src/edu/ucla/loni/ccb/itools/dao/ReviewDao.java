package edu.ucla.loni.ccb.itools.dao;

import java.util.List;

import edu.ucla.loni.ccb.itools.model.ResourceReview;

public interface ReviewDao {
	public void add(ResourceReview value);
	public void update(ResourceReview value);
	public List<ResourceReview> getResourceReviews(String resourceName);
}
