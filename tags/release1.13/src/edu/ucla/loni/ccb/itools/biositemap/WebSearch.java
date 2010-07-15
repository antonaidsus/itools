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

import edu.ucla.loni.ccb.itools.Util;
import edu.ucla.loni.ccb.itools.biositemap.htmlcrawler.HtmlCrawler;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.BioSiteMapParser;
import edu.ucla.loni.ccb.itools.parser.JdomResourceHelper;
import edu.ucla.loni.ccb.itools.parser.RdfParser;

/**
 * This class originally uses Yahoo web search engine to search biosite map on
 * the internet. Now it can crawl a website using itsucks, and parse biosite rdf
 * Registery.
 * 
 */
public class WebSearch {
	public static final Logger LOGGER = Logger.getLogger(WebSearch.class);

	WebSearchRequest request;
	String yahooApplicationID = "fYav35bV34Hr7crK7rU_HPm3tr8xlyYGvetKif2U51nsPoeqjl18DcS3hXrex7Bd";
	private long yahoo_search_timeout = 30 * 60 *1000;
	private volatile boolean yahooSearchingStopped = true;
	int returnValue = 100;
	int biositeMapUpdatedNum = 0;
	String searchTime;

	private HashMap<String, WebSearchResult> biositeMapUrls = new HashMap<String, WebSearchResult>();
	private HashMap<String, WebSearchResult> rdfMapUrls = new HashMap<String, WebSearchResult>();

	private HashSet<String> biositeMapNames = new HashSet<String>();
	private int totalResultsAvailable;

	private boolean updateRegistryOnly;

	BioSiteMapSearchHistory bioSiteMapSearchHistory;

	private boolean forceUpdate;

	private HtmlCrawler htmlCrawler;

	/**
	 * Starts search and update biosite map and biosite rdf from internet. It
	 * first uses the yahoo web search engine to search, then it uses the
	 * itSucks to crawl from site http://www.biositemap.org/
	 * 
	 */
	public void startSearch() {
		LOGGER.info("Manully started search biositemaps from internet by admin");
		updateRegistryOnly = false;
		preSearch();

		yahooSearch();

		iTsucksCrawl();

		parseBiositeRegistry();

		postSearch();
	}

	/**
	 * it reads and parses the registry data from
	 * http://www.ncbcs.org/biositemaps/biositemap.registry
	 * 
	 * Invoked periodically by Spring.
	 */
	public void startParseRegistry() {
		updateRegistryOnly = true;
		preSearch();

		parseBiositeRegistry();

		postSearch();
	}

	private void preSearch() {
		bioSiteMapSearchHistory = DaoFactory.getDaoFactory()
				.getWebSearchHistoryDao().getHistory();
		totalResultsAvailable = 0;
		biositeMapUpdatedNum = 0;
		biositeMapUrls.clear();
		rdfMapUrls.clear();
		biositeMapNames.clear();
		searchTime = new Date().toString();
	}

	private void postSearch() {
		for (WebSearchResult result : rdfMapUrls.values()) {
			newBiositeMapUrlFound(result);
		}

		for (WebSearchResult result : biositeMapUrls.values()) {
			newBiositeMapUrlFound(result);
		}
		String actual = biositeMapUrls.size() + rdfMapUrls.size() + "";
		if (!updateRegistryOnly) {
			actual += "("
					+ BiositeMapRegistry.getBiositeRdfMapUrls().size()
					+ " from registry webpage, "
					+ (htmlCrawler.getBiositeRdfMapUrls().size() + htmlCrawler
							.getBiositeMapUrls().size()) + " from Crawler) ";
		} else {
			actual += " from registry webpage.";
		}

		String resultsAvailable = updateRegistryOnly ? "-1"
				: totalResultsAvailable + "";
		bioSiteMapSearchHistory.addOneSearchSummary(searchTime,
				biositeMapUpdatedNum + "", biositeMapNames.size() + "", actual,
				resultsAvailable);

		LOGGER.info("realUrl found=" + actual + ", number of Centers="
				+ biositeMapNames.size() + ", updated number="
				+ biositeMapUpdatedNum);
		DaoFactory.getDaoFactory().getWebSearchHistoryDao().save2History(
				bioSiteMapSearchHistory);
	}

	private void parseBiositeRegistry() {
		BiositeMapRegistry.update();
		rdfMapUrls.putAll(BiositeMapRegistry.getBiositeRdfMapUrls());
	}

	private void iTsucksCrawl() {
		htmlCrawler.craw();
		if (htmlCrawler.getBiositeMapUrls().size() < 3) {
			// sometimes crawler just finished without parsing.
			htmlCrawler.craw();
		}
		biositeMapUrls.putAll(htmlCrawler.getBiositeMapUrls());
		rdfMapUrls.putAll(htmlCrawler.getBiositeRdfMapUrls());
		LOGGER.info("iTsucks Crawler found "
				+ htmlCrawler.getBiositeMapUrls().size()
				+ " biositemaps and "
				+ htmlCrawler.getBiositeRdfMapUrls().size() + " rdfs.");

	}
    
	public void turnOnYahooSearchingStopper() {		
		Thread t = new Thread(new Runnable() {
			public void run() {
				long start = System.currentTimeMillis();
				while (!yahooSearchingStopped) {
					Util.sleep(10);
					long nowtime = System.currentTimeMillis();
					if (nowtime - start  > yahoo_search_timeout) {
						yahooSearchingStopped = true;
					}
				}		
			}
		});
		
		t.start();
	}
	
	public void yahooSearch() {
		WebSearchResults results = null;
		yahooSearchingStopped = false;
		turnOnYahooSearchingStopper();
		try {
			SearchClient client = new SearchClient(yahooApplicationID);
			LOGGER.debug("Start searching biositemap. returnValue="
					+ returnValue);
			results = client.webSearch(request);

			totalResultsAvailable = results.getTotalResultsAvailable()
					.intValue();
			int totalReturned = results.getTotalResultsReturned().intValue();
			LOGGER.debug("Found " + totalResultsAvailable
					+ " hits! and return " + totalReturned);
			WebSearchResult[] results2 = results.listResults();
			workOnResults(results2, 0);
			
			while (totalReturned < totalResultsAvailable && !yahooSearchingStopped) {
				try {
					request.setStart(BigInteger.valueOf(totalReturned - 1));
					results = client.webSearch(request);
					results2 = results.listResults();
					if (results2.length == 0) {
						LOGGER.warn("Web Search return zero results, Stop searching");
						break;
					}

					workOnResults(results2, totalReturned);
					totalReturned += results2.length;
				} catch (Exception ee) {
					LOGGER.warn("Error work on yahoo search: ", ee);
					break;
				}
			}
		} catch (Exception e) {
			LOGGER.warn("Error calling Yahoo! Search Service: ", e);
		}
		LOGGER.info("websearch found " + biositeMapUrls.size()
				+ " biositemap and " + rdfMapUrls.size() + " rdf.");
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
			} else if (urlIsRdf(url)) {
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

	// the url either a biositemap.xml or biositemap.rdf
	private void newBiositeMapUrlFound(WebSearchResult result) {
		String urlStr = result.getUrl();
		JdomResourceHelper parser = null;
		try {
			if (urlIsBioSiteMap(urlStr)) {
				parser = new BioSiteMapParser();
			} else {
				parser = new RdfParser();
			}
			List<NcbcResource> resources = parser.parse(new URL(urlStr)
					.openStream());
			String biositemapName = parser.getName();
			if (biositemapName == null || biositemapName.trim().length() == 0) {
				LOGGER.info(urlStr + " is not valid, no name is defined");
				return ;
			}
			String loggingName = biositemapName + " has " + resources.size()
					+ " resources,  (" + urlStr + ") ";

			if (biositeMapNames.contains(biositemapName)) {
				LOGGER.info(loggingName + " was already processed, skipping");
				return;
			}

			biositeMapNames.add(biositemapName);

			boolean updated = bioSiteMapSearchHistory.addOneResult(searchTime,
					biositemapName, urlStr, result.getModificationDate(),
					resources.size(), forceUpdate);
			if (updated) {
				if (resources.size() > 0) {
				    LOGGER.info(loggingName + " was updated, updating iTools");
					DaoFactory.getDaoFactory().getCenterDao()
							.addExternalCenter(biositemapName);

					for (NcbcResource rsc : resources) {
						DaoFactory.getDaoFactory().getResourceDao()
								.addResource(rsc);
					}
					biositeMapUpdatedNum++;
				}
			} else {
				LOGGER.info(loggingName + " was current, no updating needed");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage() + " @newBiositeMapUrlFound for URL=" + urlStr, e);
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
	
	public long getYahooSearchTimeout() {
		return yahoo_search_timeout;
	}

	public void setYahooSearchTimeout(long l) {
		yahoo_search_timeout = l;
	}


}