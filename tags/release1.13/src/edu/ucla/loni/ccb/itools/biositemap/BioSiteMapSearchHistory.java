package edu.ucla.loni.ccb.itools.biositemap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * This class keeps records of BioSiteMap search history. It mainly keeps two
 * kinds of records. Search performed and the BioSiteMap updated.
 * 
 * @author: jeff.ma
 * @since: iTools-1.0
 * @version $Revision$ $Date$
 */
public class BioSiteMapSearchHistory {
	ArrayList<SearchSummary> searchHistory = new ArrayList<SearchSummary>();
	HashMap<String, BioSiteMapSearchRecord> bioSiteMapResearchRecords = new HashMap<String, BioSiteMapSearchRecord>();

	public Collection<BioSiteMapSearchRecord> getBioSiteMapResearchRecords() {
		return bioSiteMapResearchRecords.values();
	}

	public void addBioSiteMapResearchRecord(BioSiteMapSearchRecord record) {
		bioSiteMapResearchRecords.put(record.getName(), record);
	}

	/**
	 * It could be defined as private, so all the method are static.
	 * But it will complicate the DAO class and others.
	 */
	public BioSiteMapSearchHistory() {
	}

	public void addOneSearchSummary(String searchTime, String updated,
			String named, String actual, String total) {
		SearchSummary searchSummery = new SearchSummary(searchTime, updated,
				named, actual, total);
		searchHistory.add(searchSummery);
		// only save latest search history
		int size = searchHistory.size();
		if (size > 50) {
			ArrayList<SearchSummary> tmp = new ArrayList<SearchSummary>(51);
			int startIndex = size - 50;
			for (int i = 0; i < 50; i++) {
				tmp.add(searchHistory.get(startIndex + i));
			}
			searchHistory = tmp;
		}

	}

	public boolean addOneResult(String searchTime, String name, String url,
			String modificationDate, int numberOfRsc, boolean forcedUpdate) {
		BioSiteMapSearchRecord record = bioSiteMapResearchRecords.get(name);
		if (record == null) {
			record = new BioSiteMapSearchRecord(name);
			bioSiteMapResearchRecords.put(name, record);
		}
		return record.add(url, searchTime, modificationDate, numberOfRsc, forcedUpdate);
	}

	public ArrayList<SearchSummary> getSummaries() {
		return searchHistory;
	}

	public static class SearchSummary {
		public String searchTime;
		public String totalFound;
		public String actualBioSiteMapUrl;
		public String updated;
		public String named;

		public SearchSummary(String time, String updated, String named,
				String actual, String total) {
			searchTime = time;
			totalFound = total;
			actualBioSiteMapUrl = actual;
			this.updated = updated;
			this.named = named;
		}
	}
}
