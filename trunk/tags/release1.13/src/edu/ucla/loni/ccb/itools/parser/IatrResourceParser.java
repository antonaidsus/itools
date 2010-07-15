package edu.ucla.loni.ccb.itools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.Attributes;

import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * This is the parser of Resource, it currently only parse
 * CSV files.
 * 
 * @author Qunfei Ma
 *
 */
public class IatrResourceParser {
	public static List<NcbcResource> parseCSV(Reader reader) {
		return CSVParser.parse(reader);
	}
	
	public static List<NcbcResource> parseCSV(InputStream input) {
		return parseCSV(new InputStreamReader(input));
	}
	
	public static List<NcbcResource> parseXML(InputStream input) {
		try {
			IatrXMLResourceParser instance = new IatrXMLResourceParser();
//			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			//TODO looks like this method no more be used
//			parser.parse(input, instance);

			return instance.resources;
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		return new LinkedList<NcbcResource>();
	}


	static class IatrXMLResourceParser {
		public static final String RESOURCES="tools";
		public static final String RESOURCE="tool";		

	    private List<NcbcResource> resources = new LinkedList<NcbcResource>();
	    
		private NcbcResource currentResource;

		private StringBuffer currentBuff = new StringBuffer();
		
	    /*
	     * (non-Javadoc)
	     * 
	     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	     */
	    public void startElement(String uri, String localName, String qName,
	            Attributes atts) {
	        if (qName.equals(RESOURCE)) {
	        	currentResource = new NcbcResource();
	        } else if (!qName.equals(RESOURCES)) {
	        	currentBuff = new StringBuffer();
	        }           
	    }

	    public void endElement(String uri, String localName, String qName) {
	    	if (qName.equals(RESOURCE)) {
	    		ResourceStandardizer.standardize(currentResource);
	        	resources.add(currentResource);
	        } else if (!qName.equals(RESOURCES)) {
	        	String currentValue = currentBuff.toString().trim();   
	        	if (currentValue.length() > 0) {
        		    currentValue = StringEscapeUtils.unescapeXml(currentValue);
        		    addProperty(qName, currentValue);
	        	}
	        } 
	    }
	    
		public void characters(char[] ch, int start, int length) {
	        currentBuff.append(ch, start, length);
	    }
	    
	    private void addProperty(String name, String value) {
	    	if (name.equals("keyword") || name.equals("platform")) {
	    		String nv = currentResource.getProperty(name);
	    		if (nv == null) {
	    			nv = value;
	    		} else {
	    			nv += ", " + value;
	    		}
	    		currentResource.putProperty(name, nv);
	    	} else {
        		currentResource.putProperty(name, value);
	    	}
	    }
	}
}
