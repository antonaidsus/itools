package edu.ucla.loni.ccb.itools.biositemap;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.htmlparser.beans.StringBean;

import edu.ucla.loni.ccb.itools.biositemap.htmlcrawler.CrawlerWebSearchResult;

public class BiositeMapRegistry {
	static final Logger logger = Logger.getLogger(BiositeMapRegistry.class);

	public static final String URL_STR = "http://www.ncbcs.org/biositemaps/biositemap.registry";
	private static HashMap<String, CrawlerWebSearchResult> biositeRdfMapUrls = new HashMap<String, CrawlerWebSearchResult>();

	public static HashMap<String, CrawlerWebSearchResult> getBiositeRdfMapUrls() {
		return biositeRdfMapUrls;
	}

	public static HashMap<String, CrawlerWebSearchResult> update() {
		biositeRdfMapUrls.clear();

		StringBean sb = new StringBean();
		sb.setLinks(false);
		sb.setURL(URL_STR);
		String src = sb.getStrings();
		logger.debug("str='" + src + "'");
		
		String[] split = src.split("\\s");
		for (String urlStr : split) {
			logger.info("line='" + urlStr + "'");
			urlStr = urlStr.trim();
			if (urlStr.endsWith(".rdf")) {
				biositeRdfMapUrls.put(urlStr,
						new CrawlerWebSearchResult(urlStr));
			}

		}

		return biositeRdfMapUrls;
	}
	
	public static void main(String[] args) {
		
		System.out.println(update().size());
		
	}
}
