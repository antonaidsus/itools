package test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.yahoo.search.WebSearchRequest;

import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchHistory;
import edu.ucla.loni.ccb.itools.biositemap.WebSearch;

public class WebSearchTest {
	static BioSiteMapSearchHistory bioSiteMapSearchHistory;
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream(new File("build.properties")));
		WebSearchRequest request = new WebSearchRequest(props.getProperty("search.query"));
		request.setFormat("");
		
		WebSearch webSearch = new WebSearch();
		webSearch.setRequest(request);
		webSearch.startSearch();
	}
}
