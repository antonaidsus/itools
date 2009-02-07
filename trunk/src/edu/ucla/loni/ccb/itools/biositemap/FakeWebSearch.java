package edu.ucla.loni.ccb.itools.biositemap;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.yahoo.search.WebSearchResults;
import com.yahoo.search.xmlparser.XmlParserWebSearchResults;
import com.yahoo.xml.XmlParser;

/**
 * This class just used to return a faked search result. because I can not wait the 
 * Search engine to find the real biositemap.xml.
 * 
 * @author: jeff.ma
 * @since: iTools-1.0
 * @version $Revision$ $Date$
 */
public class FakeWebSearch {
	private static Map root = new HashMap();
	static {
		try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            XmlParser xmlParser = new XmlParser();
            parser.parse(FakeWebSearch.class.getResourceAsStream("fakedResultStr.txt"), xmlParser);
            root = xmlParser.getRoot();
	    } catch (Exception e) {
	    	
	    }
	}

	public static WebSearchResults getResults() {
		return new XmlParserWebSearchResults(root );
	}

}
