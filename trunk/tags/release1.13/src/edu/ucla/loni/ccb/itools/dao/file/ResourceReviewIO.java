package edu.ucla.loni.ccb.itools.dao.file;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.model.ResourceReview;
import edu.ucla.loni.ccb.itools.parser.ReviewParser;

/**
 * This class is actual Review DAO for filesystem. it contains all reviews for a specific NcbcResource.
 *
 * @author Jeff Qunfei Ma
 */
class ResourceReviewIO {
	public static final Logger LOGGER = Logger.getLogger(ResourceReviewIO.class);
	private static Map<String, List<ResourceReview>> loadedReviews = 
		new HashMap<String, List<ResourceReview>>();

	/**
	 * Get All reviews for the Resource named as name.
	 * @param name Resource name
	 * @return
	 */
	public static List<ResourceReview> getReviews(String name) {
		List<ResourceReview> reviews = loadedReviews.get(name);
		if (reviews != null) {
			return reviews;
		}
		
		String filename = getStreamName(name);
		reviews = new ArrayList<ResourceReview>(0);
		try {
			InputStream input = new FileInputStream(filename);
			reviews = ReviewParser.parse(input);
			loadedReviews.put(name, reviews);
			input.close();
		} catch (Exception e) {
			LOGGER.warn("can't load reviews for resource:" + name + e);
		}
		return reviews;		
	}


//	private ResourceReviewIO(String inputname) {
//		streamName = inputname;
//	}
//	
//	public int getNumberofComments() {
//		return resourceReviews.size();
//	}
//	
//	public List getResourceReviews() {
//		return resourceReviews;
//	}
//	
//	public int getAverageRate() {
//		if (resourceReviews.size() == 0) {
//			return 8; //default 4 star;
//		}
//		
//		if (averageRate == -1) {
//			int total = 0;
//			for (Iterator iter = resourceReviews.iterator(); iter.hasNext();) {
//				ResourceReview el = (ResourceReview) iter.next();
//				total += el.getRate();
//			}
//			averageRate = total / resourceReviews.size();
//		}
//		return averageRate;
//	}
	
//	public ResourceReview get(int index) {
//		return (ResourceReview) resourceReviews.get(index);
//	}

	private static String getStreamName(String name) {
		return NcbcResourceIO.getReviewSaveDir() + '/' + name.replaceAll(" ", "_") + ".xml";
	}
	
	public static void add(ResourceReview value) {
		List<ResourceReview> reviews = loadedReviews.get(value.getResourceName());

		if (reviews == null) {
			reviews = new LinkedList<ResourceReview>();
			loadedReviews.put(value.getResourceName(), reviews);
		} 
		
		reviews.add(value);
		String filename = getStreamName(value.getResourceName());
		save2Disk(reviews, filename);
	}
	
	public static void update(ResourceReview value) {
		List<ResourceReview> reviews = loadedReviews.get(value.getResourceName());
		if (reviews == null) {
			LOGGER.warn("no reviews are ever read");
			return;
		} 
		//remove the value first
		reviews.remove(value);
		reviews.add(value);
		save2Disk(reviews, getStreamName(value.getResourceName()));
	}

	private static void save2Disk(List<ResourceReview> reviews, String filename) {
		try {
			FileWriter fout = new FileWriter(filename);
			fout.write(ReviewParser.toXML(reviews));
			fout.close();
		} catch (IOException e) {
			LOGGER.error(e,e);
		}
	}	
}
