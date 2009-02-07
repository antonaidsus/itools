package edu.ucla.loni.ccb.itools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;

import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchHistory;
import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchRecord;
import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchHistory.SearchSummary;

public class BioSiteMapSearchHistoryParser extends MyXmlParser {
	BioSiteMapSearchHistory bioSiteMapSearachHistory = new BioSiteMapSearchHistory();
	BioSiteMapSearchRecord bioSiteMap;
	
	public static String toXML(BioSiteMapSearchHistory searchHistory) {
    	StringBuffer bf = new StringBuffer(startTag("SearchHistory") + "\n");
    	for (Iterator iter = searchHistory.getSummaries().iterator(); iter.hasNext();) {
			SearchSummary inst = (SearchSummary) iter.next();
			bf.append(S4 + "<Search searchDate=\"" + inst.searchTime + "\"" +
					" updated=\"" + inst.updated + "\"" +
					" named=\"" + inst.named + "\"" +
					" actualUrl=\"" + inst.actualBioSiteMapUrl +"\"" +
					" totalFound=\"" + inst.totalFound +"\"/>\n");			
		}
    	
    	for (Iterator iter = searchHistory.getBioSiteMapResearchRecords().iterator(); iter.hasNext();) {
			BioSiteMapSearchRecord record = (BioSiteMapSearchRecord) iter.next();
    		bf.append(record.toXML(S4));
		}
    	bf.append(endTag("SearchHistory"));
    	return bf.toString();
    }
    
    public void startElement(String uri, String localName, String qName,
            Attributes atts) {
    	if (qName.equals("Search")) {
    		SearchSummary inst = new BioSiteMapSearchHistory.SearchSummary(atts.getValue("searchDate"),
    				atts.getValue("updated"),atts.getValue("named"), atts.getValue("actualUrl"), atts.getValue("totalFound"));
    		bioSiteMapSearachHistory.getSummaries().add(inst);
    	} else if (qName.equals("bioSiteMap")) {
    		bioSiteMap = 
    			new BioSiteMapSearchRecord(atts.getValue("name"));
    		BioSiteMapSearchHistory.addBioSiteMapResearchRecord(bioSiteMap);
    	} else if (qName.equals("history")) {
    		BioSiteMapSearchRecord.Record record = new BioSiteMapSearchRecord.Record(
    				atts.getValue("url"), atts.getValue("updateTime"),atts.getValue("lastModified"), -1);
    		bioSiteMap.add(record);
    	}
    }
    
    public static BioSiteMapSearchHistory parse(InputStream input) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			BioSiteMapSearchHistoryParser inst = new BioSiteMapSearchHistoryParser();
			parser.parse(input, inst);
			return inst.bioSiteMapSearachHistory;
		} catch (Exception e) {
			LOGGER.warn(e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				LOGGER.warn(e);
			}
		}
		return null;
    }

}
