package edu.ucla.loni.ccb.itools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import edu.ucla.loni.ccb.itools.model.NcbcCenter;

/**
 * 
 * This class read and parse the center definition from a Stream.
 * @author Jeff Qunfei Ma
 *
 */
public class CentersParser extends JdomHelper {
	private static final Logger logger = Logger.getLogger(CentersParser.class);

	public static final String CENTER = "center";
	private List<NcbcCenter> centers = new LinkedList<NcbcCenter>();

	public static List<NcbcCenter> parse(InputStream input) {
		CentersParser inst = new CentersParser();
		try {
			Document document = buildDocument(new InputStreamReader(input));
			Element rootElement = document.getRootElement();
			for (Element ele : getChildren(rootElement)) {
				inst.centers.add( jdom2Center(ele));
			}
		} catch (Exception e) {
			logger.warn(e, e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				logger.warn(e, e);
			}
		}
		return inst.centers;
	}
	
	public static NcbcCenter jdom2Center(Element ele) {
		NcbcCenter center = new NcbcCenter();
		center.setName(ele.getAttributeValue("name"));  
		center.setFullName(ele.getAttributeValue("fullName"));  
		center.setUrl(ele.getAttributeValue("url"));
		center.setHomeUrl(ele.getAttributeValue("home"));
		if ("true".equalsIgnoreCase(ele.getAttributeValue("external"))) {
		    center.setExternal(true);
		}

		return center;
	}

	public static String toXml(List<NcbcCenter> centers) {
		Element root = new Element("centers");
		for (NcbcCenter center : centers) {
			root.addContent(center2Jdom(center));		
		}
		
		return element2String(root);		
	}
	
	public static Element center2Jdom(NcbcCenter center) {
		Element ele = new Element(CENTER);
		setAttribute(ele,"name", center.getName());
		if (center.isExternal()) {
			setAttribute(ele,"external", "true");
		}

		if (center.getFullName() != null) {
			setAttribute(ele,"fullName", center.getFullName());
		}
		
		if (center.getHomeUrl() != null) {
			setAttribute(ele,"home", center.getHomeUrl());
		}
		
		if (center.getUrl() != null) {
			setAttribute(ele,"url",center.getUrl());
		}
		return ele;
	}

	public static String getSaveString(NcbcCenter center) {
		return element2String(center2Jdom(center));
	}
	
}
