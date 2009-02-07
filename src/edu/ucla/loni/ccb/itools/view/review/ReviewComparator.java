package edu.ucla.loni.ccb.itools.view.review;

import java.util.Comparator;
import java.util.HashMap;

import edu.ucla.loni.ccb.itools.model.ResourceReview;

public abstract class ReviewComparator implements Comparator {
	static String[] orderStrs= 
	{"Nearest First","Oldest First", "Highest Rating First", "Lowest Rating First"};
	
	static HashMap instances = new HashMap();
	static {
		instances.put("Nearest First", new NearestFirst());
		instances.put("Oldest First", new OldestFirst());
		instances.put("Highest Rating First", new HighestRatingFirst());
		instances.put("Lowest Rating First", new LowestRatingFirst());
	}

    public static String[] getImplementedNames() {
    	return orderStrs;
    }
    
    public static ReviewComparator getInstanceBtName(String name) {
    	return (ReviewComparator) instances.get(name);
    }
    
	static class OldestFirst extends ReviewComparator {
		public int compare(Object o1, Object o2) {
			return ((ResourceReview) o1).getCreatedDate().compareTo(
					((ResourceReview) o2).getCreatedDate());
		}
	}
	static class NearestFirst extends ReviewComparator {
		public int compare(Object o1, Object o2) {
			return -((ResourceReview) o1).getCreatedDate().compareTo(
					((ResourceReview) o2).getCreatedDate());
		}
	}
	
	static class HighestRatingFirst extends ReviewComparator {
		public int compare(Object o1, Object o2) {
			return ((ResourceReview) o1).getRate() -
					((ResourceReview) o2).getRate();
		}
	}
	
	static class LowestRatingFirst extends ReviewComparator {
		public int compare(Object o1, Object o2) {
			return ((ResourceReview) o2).getRate() -
					((ResourceReview) o1).getRate();
		}
	}

}
