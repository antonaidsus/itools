package edu.ucla.loni.ccb.itools.biositemap.htmlcrawler;

import java.util.Date;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.HeadMethod;

import com.yahoo.search.CacheInfo;
import com.yahoo.search.WebSearchResult;

public class CrawlerWebSearchResult implements WebSearchResult {
	private static HttpClient client = new HttpClient();

    private String urlString;
    private String lastModifiedTime;
    public CrawlerWebSearchResult(String urlStr) {
    	urlString = urlStr;
    }
	public CrawlerWebSearchResult(String urlStr, String lmt) {
		urlString = urlStr;
		lastModifiedTime = lmt;
	}

	public CacheInfo getCache() {
		return null;
	}


	public String getClickUrl() {
		return urlString;
	}


	public String getMimeType() {
		return "text/xml";
	}

	public String getModificationDate() {
		return lastModifiedTime;
	}


	public String getSummary() {
		return "Crawler created Web Search result summary";
	}


	public String getTitle() {
		return "Crawler created Web Search result title";
	}

	public String getUrl() {
		return urlString;
	}
	public static String getLastModifiedTime(String urlStr) {		
		String lastModified = null;
		
		try {
			HeadMethod head = new HeadMethod(urlStr);
			client.executeMethod(head);
			Header responseHeader = head.getResponseHeader("last-modified");
			
			if (responseHeader != null) {
				String tmp = responseHeader.getValue();
				lastModified = new Date(tmp).getTime() + "";
				// lastModified =
				// DateFormat.getDateInstance().parse(tmp).getTime() + "";
			}
			
		} catch (Exception e) {
			//mLog.info("Failed to get Last modified time due to " + e);
		}
		return lastModified;
		
	}

}
