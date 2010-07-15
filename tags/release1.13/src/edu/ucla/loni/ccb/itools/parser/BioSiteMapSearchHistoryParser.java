package edu.ucla.loni.ccb.itools.parser;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchHistory;
import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchRecord;
import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchHistory.SearchSummary;
import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchRecord.Record;

public class BioSiteMapSearchHistoryParser extends JdomHelper {
	private static final Logger logger = Logger.getLogger(BioSiteMapSearchHistoryParser.class);
	BioSiteMapSearchHistory history = new BioSiteMapSearchHistory();
	BioSiteMapSearchRecord bioSiteMap;
	
	public static String toXML(BioSiteMapSearchHistory searchHistory) {
		Element ele = new Element("SearchHistory");
    	for (SearchSummary inst : searchHistory.getSummaries()) {
    		ele.addContent(summery2Jdom(inst));
		}
    	
    	for (BioSiteMapSearchRecord record : searchHistory.getBioSiteMapResearchRecords()) {
    		ele.addContent(record2Jdom(record));
		}

       return element2String(ele);
    }
    
    public static Element record2Jdom(BioSiteMapSearchRecord biositeMap) {
    	Element ele = new Element("bioSiteMap");
    	setAttribute(ele,"name", biositeMap.getName());
    	for (Record record : biositeMap.getRecords()) {
    		Element r = new Element("history");
    		setAttribute(r,"url", record.url);
    		setAttribute(r,"lastModified", record.modifiedTime);
    		setAttribute(r,"numberOfResource", record.numberOfResource + "");
    		setAttribute(r,"updateTime", record.actionTime);
    		ele.addContent(r);
    	}
		return ele;
	}

	public static Element summery2Jdom(SearchSummary inst) {
    	Element ele = new Element("Search");
    	setAttribute(ele,"searchDate", inst.searchTime);
    	setAttribute(ele,"updated", inst.updated );
    	setAttribute(ele,"named", inst.named);
    	setAttribute(ele,"actualUrl", inst.actualBioSiteMapUrl);
    	setAttribute(ele,"entriesFoundByYahoo", inst.totalFound);			

		return ele;
	}
	
    public static SearchSummary jdom2Summary(Element ele) {
    	String value = ele.getAttributeValue("totalFound");
		if (value == null) {
			value = ele.getAttributeValue("entriesFoundByYahoo");
		}
		return new BioSiteMapSearchHistory.SearchSummary(
				ele.getAttributeValue("searchDate"),
				ele.getAttributeValue("updated"),
				ele.getAttributeValue("named"), 
				ele.getAttributeValue("actualUrl"),
				value);
	}

	public static BioSiteMapSearchRecord jdom2BioSiteMap(Element ele) {
		BioSiteMapSearchRecord bioSiteMap = 
			new BioSiteMapSearchRecord(ele.getAttributeValue("name"));
		for (Element e : getChildren(ele)) {
			BioSiteMapSearchRecord.Record record = new BioSiteMapSearchRecord.Record(
    				e.getAttributeValue("url"), 
    				e.getAttributeValue("updateTime"),
    				e.getAttributeValue("lastModified"),
    				-1);
    		bioSiteMap.add(record);
		}
		
		return bioSiteMap;
	}
    
	public static BioSiteMapSearchHistory parse(InputStream input) {
		BioSiteMapSearchHistory history = new BioSiteMapSearchHistory();

		try {
			Document document = buildDocument(new InputStreamReader(input));
			Element rootElement = document.getRootElement();
			for (Element ele : getChildren(rootElement)) {
				String name = ele.getName();
				if (name.equals("Search")) {
					history.getSummaries().add(jdom2Summary(ele));
				} else if (name.equals("bioSiteMap")) {
					history.addBioSiteMapResearchRecord(jdom2BioSiteMap(ele));
				} 
			}
		} catch (Exception e) {
			logger.warn(e, e);
		} finally {
			try {
				input.close();
			} catch (Exception e) {
				logger.warn(e, e);
			}			
		}
		return history;
	}
}
