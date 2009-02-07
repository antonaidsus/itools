package edu.ucla.loni.ccb.itools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom.Element;
import org.xml.sax.Attributes;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * 
 * This class reads NcbcResource in XML format.
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class NcbcResourceParser extends MyXmlParser {
	public static final String RESOURCES = "ncbcresources";
	public static final String RESOURCE = "ncbcresource";
	public static final String PROPERTY = "property";
	public static final String NAME = "name";
	public static final String VALUE = "value";

	private List<NcbcResource> resources = new LinkedList<NcbcResource>();

	private NcbcResource currentResource;
	private String currentName;
	private String currentValue;
	private StringBuffer currentBuff = new StringBuffer();

	private Stack<String> parsingElements = new Stack<String>();

	public static List<NcbcResource> parse(InputStream input) {
		try {
			NcbcResourceParser instance = new NcbcResourceParser();
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(input, instance);

			return instance.resources;
		} catch (Exception e) {
			LOGGER.warn(e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				LOGGER.warn(e);
			}
		}
		return new ArrayList<NcbcResource>(0);
	}

	public static String getSaveString(List<? extends NcbcResource> resources) {
		StringBuffer buff = new StringBuffer();
		buff.append("<" + RESOURCES + ">\n");
		for ( NcbcResource element : resources) {
			buff.append(getSaveString(element, ""));
		}
		buff.append("</" + RESOURCES + ">\n");
		return buff.toString();
	}

	static Element createPropertyElement(NcbcResource rsc, String propertyName) {
		String value = StringEscapeUtils.escapeXml(rsc
				.getProperty(propertyName));
		propertyName = StringEscapeUtils.escapeXml(propertyName);

		Element propE = new Element(PROPERTY);

		Element nameE = JdomResourceHelper.textElement(NAME, propertyName);
		Element valueE = JdomResourceHelper.textElement(VALUE, value);

		propE.addContent(nameE);
		propE.addContent(valueE);

		return propE;
	}

	public NcbcResource jdom2Resource(Element ele) {
		NcbcResource rsc = new NcbcResource();
		Iterator<?> children = ele.getChildren(PROPERTY).iterator();
		while (children.hasNext()) {
			Element prop = (Element) children.next();
			rsc.putProperty(prop.getChildText(NAME), prop.getChildText(VALUE));
		}

		return rsc;
	}

	public Element resouce2Jdom(NcbcResource rsc) {
		Element ele = new Element(RESOURCE);
		ele.setAttribute("center", rsc.getCenter());
		ele.setAttribute("id", rsc.getId() + "");

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

	public static String getSaveString(NcbcResource rsc, String idt) {
		StringBuffer buff = new StringBuffer(idt + "<" + RESOURCE
				+ " center=\"" + rsc.getCenter() + "\" id=\"" + rsc.getId()
				+ "\" >\n");
		Set<String> required = Descriptor.getRequiredDescriptorNames();
		for (String str : required) {
			append2TheBuffer(rsc, idt, buff, str);
		}

		Set<String> allkeys = rsc.getAllProperties().keySet();
		LinkedList<String> left = new LinkedList<String>(allkeys);
		left.removeAll(required);

		for (String str :  left) {
			append2TheBuffer(rsc, idt, buff, str);
		}
		buff.append(idt + "</" + RESOURCE + ">\n");
		return buff.toString();
	}

	private static void append2TheBuffer(NcbcResource rsc, String idt,
			StringBuffer buff, String name) {
		String value = rsc.getProperty(name);

		if (value != null && value.length() > 0) {
			name = StringEscapeUtils.escapeXml(name);
			value = StringEscapeUtils.escapeXml(value);

			buff.append(idt + S4 + "<" + PROPERTY + ">\n" + idt + S8 + "<"
					+ NAME + ">" + name + "</" + NAME + ">\n" + idt + S8 + "<"
					+ VALUE + ">" + value + "</" + VALUE + ">\n" + idt + S4
					+ "</" + PROPERTY + ">");
		}
	}

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

			for (int i = 0, I = atts.getLength(); i < I; i++) {
				String a = atts.getQName(i);
				if (a.equals("center")) {
					currentResource.setCenter(atts.getValue(i));
				} else if (a.equals("id")) {
					currentResource.setId(Integer.parseInt(atts.getValue(i)));
				}
			}
		} else if (qName.equals(PROPERTY)) {

		} else if (qName.equals(NAME) || qName.equals(VALUE)) {
			currentBuff = new StringBuffer();
			parsingElements.push(qName);
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (qName.equals(RESOURCE)) {
			if (currentResource.isValid()) {
				currentResource.simplifyOntology();
			    resources.add(currentResource);
			} else {
				LOGGER.warn("This resource not valid\n" + getSaveString(currentResource, ""));
			}
		} else if (qName.equals(PROPERTY)) {
			if (currentName != null && currentValue != null) {
				currentResource.putProperty(currentName, currentValue);
			}
			currentName = null;
			currentValue = null;
		} else if (qName.equals(NAME) || qName.equals(VALUE)) {
			Object name = parsingElements.pop();
			if (name.equals(NAME)) {
				currentName = StringEscapeUtils.unescapeXml(
						currentBuff.toString()).trim();
			} else if (name.equals(VALUE)) {
				currentValue = currentBuff.toString();
				// if (currentValue.indexOf("--") != -1) {
				// LOGGER.warn("orignal:" + currentValue + "indexOf(-->) "
				// +currentValue.indexOf("-->"));
				// LOGGER.warn("after:" +
				// StringEscapeUtils.unescapeXml(currentValue));
				// }
				currentValue = StringEscapeUtils.unescapeXml(currentValue)
						.trim();
			}
		}
	}

	public void characters(char[] ch, int start, int length) {
		currentBuff.append(ch, start, length);
	}

}
