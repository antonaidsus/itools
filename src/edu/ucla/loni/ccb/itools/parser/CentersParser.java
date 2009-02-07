package edu.ucla.loni.ccb.itools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;

import edu.ucla.loni.ccb.itools.model.NcbcCenter;

/**
 * 
 * This class read and parse the center definition from a Stream.
 * @author Jeff Qunfei Ma
 *
 */
public class CentersParser extends MyXmlParser {
	public static final String CENTER = "center";
	private List<NcbcCenter> centers = new LinkedList<NcbcCenter>();

	public static List<NcbcCenter> parse(InputStream input) {
		CentersParser centersParser = new CentersParser();

		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(input, centersParser);
		} catch (Exception e) {
			LOGGER.warn(e, e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				LOGGER.warn(e, e);
			}
		}
		return centersParser.centers;
	}
	
	public static String toXml(List<NcbcCenter> centers) {
		StringBuffer bf = new StringBuffer(startTag("centers") + "\n");
		for (NcbcCenter center : centers) {
			bf.append( getSaveString(S4, center));		
		}
		bf.append(endTag("centers"));
		
		return bf.toString();		
	}
	
	private static String getSaveString(String id, NcbcCenter center) {
		StringBuffer bf = new StringBuffer(id + "<center name=\"" + center.getName() + "\" ");
		if (center.isExternal()) {
			bf.append("external=\"true\" ");
		}
		if (center.getHomeUrl() != null) {
			bf.append("home=\"" + center.getHomeUrl() +"\"\n");
		}
		
		if (center.getUrl() != null) {
			bf.append(id + S4+  "url=\"" + center.getUrl() + "\"");
		}
		bf.append(id + "/>\n");
		
		return bf.toString();
	}
	
    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
            Attributes atts) {
        if (qName.equals(CENTER)) {
        	NcbcCenter center = new NcbcCenter();
            for (int i = 0, I = atts.getLength(); i < I; i++) {
                String a = atts.getQName(i);
                String v = atts.getValue(i);
                if (a.equalsIgnoreCase("name")) {
                	center.setName(v);               	
                } else if (a.equalsIgnoreCase("url")) {
                	center.setUrl(v);
                } else if (a.equalsIgnoreCase("home")) {
                	center.setHomeUrl(v);
                } else if (a.equalsIgnoreCase("external")) {
                	center.setExternal(v.equalsIgnoreCase("true"));
                }
            }
            centers.add(center);
        } 
    }
}
