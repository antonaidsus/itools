package edu.ucla.loni.ccb.itools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * 
 * This class reads NcbcResource in XML format.
 * The format is original of 
 *  <property>
 *     <name>name</name>
 *     <value>value </value>
 * </property>
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class NcbcResourceParser extends JdomHelper{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(NcbcResourceParser.class);

	public static final String RESOURCES = "ncbcresources";
	public static final String RESOURCE = "ncbcresource";
	public static final String PROPERTY = "property";
	public static final String NAME = "name";
	public static final String VALUE = "value";
	public static final String ID ="id";
	public static final String CENTER ="center";

	public static List<NcbcResource> parse(InputStream input) {
		List<NcbcResource> rv = Collections.emptyList();
		try {
			rv = new ArrayList<NcbcResource>();
			Document document = buildDocument(new InputStreamReader(input));
			Element rootElement = document.getRootElement();
			
			if (rootElement.getName().equals(RESOURCES)) {
				for (Element rscEle : getChildren(rootElement)) {
					NcbcResource rsc = jdom2Resource(rscEle);
					if (rsc.isValid()) {
						rsc.simplifyOntology();
					    rv.add(rsc);
					}
				}
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				logger.warn(e.getMessage());
			}
		}
		return rv;
	}
	
	public static NcbcResource jdom2Resource(Element ele) {
		NcbcResource rsc = new NcbcResource();
		rsc.setCenter(ele.getAttributeValue(CENTER));
		rsc.setId(Integer.parseInt(ele.getAttributeValue(ID)));
		for (Element prop : JdomHelper.getChildren(ele, PROPERTY)) {
			rsc.putProperty(prop.getChildText(NAME),prop.getChildText(VALUE));
		}

		return rsc;
	}


	public static String getSaveString(List<? extends NcbcResource> resources) {
		Element root = new Element(RESOURCES);
		List<Element> content = new ArrayList<Element>(resources.size());
		for (NcbcResource rsc : resources) {
			content.add(resouce2Jdom(rsc));
		}
		root.addContent(content);
		return element2String(root);
	}

	static Element createPropertyElement(NcbcResource rsc, String propertyName) {
		String value = rsc.getProperty(propertyName);

		Element propE = new Element(PROPERTY);

		Element nameE = textElement(NAME, propertyName);
		Element valueE = textElement(VALUE, value);

		propE.addContent(nameE);
		propE.addContent(valueE);

		return propE;
	}

	public static Element resouce2Jdom(NcbcResource rsc) {
		Element ele = new Element(RESOURCE);
		ele.setAttribute(CENTER, rsc.getCenter());
		ele.setAttribute(ID, rsc.getId()+"");

		Set<String> required = Descriptor.getRequiredDescriptorNames();
		for (String str : required) {
			Element propE = createPropertyElement(rsc, str);
			ele.addContent(propE);
		}

		Set<String> allkeys = rsc.getAllProperties().keySet();
		LinkedList<String> left = new LinkedList<String>(allkeys);
		left.removeAll(required);

		for (String str :  left) {
			Element propE = createPropertyElement(rsc, str);
			ele.addContent(propE);
		}
		return ele;
	}

	public static String getSaveString(NcbcResource rsc) {
		Element ele = resouce2Jdom(rsc);
		return element2String(ele);		
	}
}
