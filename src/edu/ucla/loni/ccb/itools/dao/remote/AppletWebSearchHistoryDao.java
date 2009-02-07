package edu.ucla.loni.ccb.itools.dao.remote;

import java.io.ByteArrayInputStream;

import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchHistory;
import edu.ucla.loni.ccb.itools.dao.WebSearchHistoryDao;
import edu.ucla.loni.ccb.itools.parser.BioSiteMapSearchHistoryParser;

public class AppletWebSearchHistoryDao implements WebSearchHistoryDao {

	public BioSiteMapSearchHistory getHistory() {
		String str = ProxyServer.talk2Server("websearch.htm?method=getBioSiteMapHistory");
        return BioSiteMapSearchHistoryParser.parse(new ByteArrayInputStream(str.getBytes()));
	}

	public void save2History(BioSiteMapSearchHistory history) {
		// TODO Auto-generated method stub

	}

}
