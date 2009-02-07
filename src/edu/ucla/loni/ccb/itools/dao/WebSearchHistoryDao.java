package edu.ucla.loni.ccb.itools.dao;

import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchHistory;

public interface WebSearchHistoryDao {
	BioSiteMapSearchHistory getHistory();
	void save2History(BioSiteMapSearchHistory history);
}
