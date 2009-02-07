package edu.ucla.loni.ccb.itools.biositemap;

import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.yahoo.search.SearchClient;
import com.yahoo.search.WebSearchRequest;
import com.yahoo.search.WebSearchResult;
import com.yahoo.search.WebSearchResults;

import edu.ucla.loni.ccb.itools.biositemap.htmlcrawler.CrawlerWebSearchResult;
import edu.ucla.loni.ccb.itools.biositemap.htmlcrawler.HtmlCrawler;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.BioSiteMapParser;
import edu.ucla.loni.ccb.itools.parser.JdomResourceHelper;
import edu.ucla.loni.ccb.itools.parser.RdfParser;

/**
 */
public class WebSearch {
	public static final Logger LOGGER = Logger.getLogger(WebSearch.class);

	int returnValue = 100;
	WebSearchRequest request;
	String yahooApplicationID = "fYav35bV34Hr7crK7rU_HPm3tr8xlyYGvetKif2U51nsPoeqjl18DcS3hXrex7Bd";
	int biositeMapUpdatedNum = 0;
	String searchTime;

	private HashMap<String, WebSearchResult> biositeMapUrls = new HashMap<String, WebSearchResult>();
	private HashMap<String, WebSearchResult> rdfMapUrls = new HashMap<String, WebSearchResult>();
	
	private HashSet<String> biositeMapNames = new HashSet<String>();
	private int totalResultsAvailable;

	private boolean fakeSearch = false;

	BioSiteMapSearchHistory bioSiteMapSearchHistory;

	private boolean forceUpdate;
	
	private HtmlCrawler htmlCrawler;

	public void startSearch() {
		SearchClient client = new SearchClient(yahooApplicationID);
		bioSiteMapSearchHistory = DaoFactory.getDaoFactory()
				.getWebSearchHistoryDao().getHistory();
		biositeMapUpdatedNum = 0;
		biositeMapUrls.clear();
		rdfMapUrls.clear();
		biositeMapNames.clear();
		searchTime = new Date().toString();

		try {
			WebSearchResults results = null;
			if (fakeSearch) {
				results = FakeWebSearch.getResults();
			} else {
				LOGGER.debug("Start searching biositemap. returnValue=" + returnValue);
				results = client.webSearch(request);
			}

			totalResultsAvailable = results.getTotalResultsAvailable()
					.intValue();
			int totalReturned = results.getTotalResultsReturned().intValue();
			LOGGER.debug("Found " + totalResultsAvailable
					+ " hits! and return " + totalReturned);
			WebSearchResult[] results2 = results.listResults();
			workOnResults(results2, 0);

			while (totalReturned < totalResultsAvailable) {
				try {
					request.setStart(BigInteger.valueOf(totalReturned - 1));
					results = client.webSearch(request);
					results2 = results.listResults();
					workOnResults(results2, totalReturned);
					totalReturned += results2.length;
				} catch (Exception ee) {
					LOGGER.warn("Error calling Yahoo! Search Service: ", ee);
				}
			}
			LOGGER.info("websearch found " + biositeMapUrls.size() + " biositemap and " + rdfMapUrls.size() + " rdf.");
			
			HashMap<String, CrawlerWebSearchResult> biositeMapUrls2 = htmlCrawler.getBiositeMapUrls();
			HashMap<String, CrawlerWebSearchResult> biositeRdfMapUrls = htmlCrawler.getBiositeRdfMapUrls();
			if (htmlCrawler != null) {
				htmlCrawler.craw();
				if (biositeRdfMapUrls.size() < 3) {
					//sometimes  crawler just finished without parsing.
					htmlCrawler.craw();
					biositeMapUrls2 = htmlCrawler.getBiositeMapUrls();
					biositeRdfMapUrls = htmlCrawler.getBiositeRdfMapUrls();
				}
				LOGGER.info("Crawler found " + biositeMapUrls2.size() + " biositemap and " + biositeRdfMapUrls.size() + " rdf.");
				biositeMapUrls.putAll(biositeMapUrls2);
				rdfMapUrls.putAll(biositeRdfMapUrls);
			}
				
			HashMap<String, CrawlerWebSearchResult> fromRegistry = BiositeMapRegistry.update();
			rdfMapUrls.putAll( fromRegistry);
			
			for (WebSearchResult result : rdfMapUrls.values()) {
				newBiositeMapUrlFound(result);
			}
			
			for (WebSearchResult result : biositeMapUrls.values() ) {
				newBiositeMapUrlFound(result);
			}
			
			bioSiteMapSearchHistory.addOneSearchSummary(searchTime, biositeMapUpdatedNum
					+ "", biositeMapNames.size() + "", (biositeMapUrls.size() + rdfMapUrls.size()) 
					+ "(" +  fromRegistry.size() + " from Regisotry, and " + biositeRdfMapUrls.size() + " from Crawler) ",
					totalResultsAvailable + "");
			LOGGER.info("realUrl found=" + (biositeMapUrls.size() + rdfMapUrls.size())
					+ ", number of Centers=" + biositeMapNames.size()
					+ ", updated number=" + biositeMapUpdatedNum);
			DaoFactory.getDaoFactory().getWebSearchHistoryDao().save2History(
					bioSiteMapSearchHistory);
		} catch (Exception e) {
			// An issue with the XML or with the service.
			LOGGER.warn("Error calling Yahoo! Search Service: ", e);
		} finally {
			biositeMapUrls.clear();
			biositeMapNames.clear();
		}
	}

	protected void workOnResults(WebSearchResult[] results, int offset) {
		LOGGER.debug("Display " + (offset + 1) + " to "
				+ (offset + results.length));

		for (int i = 0; i < results.length; i++) {
			WebSearchResult result = results[i];
			String url = result.getUrl();
			LOGGER.debug((offset + i + 1) + ": " + " url= " + url);

			
			if (urlIsBioSiteMap(url)) {
				biositeMapUrls.put(url, result);
			} else if (urlIsRdf(url)){
				rdfMapUrls.put(url, result);
			}
		}
	}

	private boolean urlIsBioSiteMap(String url) {
		return url.toLowerCase().endsWith("biositemap.xml");
	}

	private boolean urlIsRdf(String url) {
		return url.toLowerCase().endsWith("biositemap.rdf");
	}
	
	//the url either a biositemap.xml or biositemap.rdf
	private void newBiositeMapUrlFound(WebSearchResult result) {
		String urlStr = result.getUrl();
		JdomResourceHelper parser = null;
		try {
			if (urlIsBioSiteMap(urlStr)) {
			   parser = new BioSiteMapParser();
			} else {
			   parser = new RdfParser();
			}
			List<NcbcResource> resources = parser.parse(new URL(urlStr).openStream());
			String biositemapName = parser.getName();
			String loggingName = biositemapName + " has " + resources.size() + " resources,  (" + urlStr + ") "; 

			if (biositeMapNames.contains(biositemapName)) {
				LOGGER.info(loggingName + " was already processed, skipping");
				return;
			}

			biositeMapNames.add(biositemapName);

			boolean updated = bioSiteMapSearchHistory.addOneResult(searchTime,
					biositemapName, urlStr, result.getModificationDate(), resources.size(), forceUpdate);
			if (updated) {
				LOGGER.info(loggingName + " was updated, updating iTools");
				DaoFactory.getDaoFactory().getCenterDao().addExternalCenter(
						biositemapName);
				for (NcbcResource rsc : resources) {
					DaoFactory.getDaoFactory().getResourceDao()
							.addResource(rsc);
				}
				biositeMapUpdatedNum++;
			} else {
				LOGGER.info(loggingName
								+ " was current, no updating needed");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage() + " @newBiositeMapUrlFound", e);
		}
	}
	
	public WebSearchRequest getRequest() {
		return request;
	}

	public void setRequest(WebSearchRequest request) {
		this.request = request;
	}

	public String getYahooApplicationID() {
		return yahooApplicationID;
	}

	public void setYahooApplicationID(String yahooApplicationID) {
		this.yahooApplicationID = yahooApplicationID;
	}

	public static void main(String[] args) {
		// Create the web search request.
		String query = "biositemap";
		WebSearchRequest request = new WebSearchRequest(query);

		WebSearch webSearch = new WebSearch();
		request.setResults(webSearch.returnValue);

		webSearch.setYahooApplicationID("javasdktest");
		webSearch.setRequest(request);
		webSearch.startSearch();
	}

	public boolean isFakeSearch() {
		return fakeSearch;
	}

	public void setFakeSearch(boolean fakeSearch) {
		this.fakeSearch = fakeSearch;
	}

	public int getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(int returnValue) {
		this.returnValue = returnValue;
	}

	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}
	
	public void setHtmlCrawler(HtmlCrawler crawler) {
		htmlCrawler = crawler;
	}

}