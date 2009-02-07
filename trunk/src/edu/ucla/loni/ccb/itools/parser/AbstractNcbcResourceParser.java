package edu.ucla.loni.ccb.itools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.Attributes;

import edu.ucla.loni.ccb.itools.model.NcbcResource;

public abstract class AbstractNcbcResourceParser extends MyXmlParser {
	protected NcbcResource currentResource;
	protected String currentName;
	protected String currentValue;
	protected StringBuffer currentBuff = new StringBuffer();

	protected Stack parsingElements = new Stack();

	protected List resources = new LinkedList();

	protected List doParse(InputStream input) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			parser.parse(input, this);
			return getResources();
		} catch (Exception e) {
			LOGGER.warn(e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				LOGGER.warn(e);
			}
		}
		return new ArrayList(0);
	}

	public List getResources() {
		return resources;
	}

	protected void resourceTagStarted(Attributes atts) {
		currentResource = new NcbcResource();

		for (int i = 0, I = atts.getLength(); i < I; i++) {
			String a = atts.getQName(i);
			if (a.equals("center")) {
				currentResource.setCenter(atts.getValue(i));
			} else if (a.equals("id")) {
				currentResource.setId(Integer.parseInt(atts.getValue(i)));
			}
		}
	}

	protected void resourceTagEnded() {
		standardize(currentResource);
		resources.add(currentResource);
	}

	protected void standardize(NcbcResource rsc) {

	}

	protected void catchingCharactersStarted(String uri, String localName,
			String qName, Attributes atts) {
		currentBuff = new StringBuffer();
		parsingElements.push(localName);
	}

	protected boolean isTagaKey(String str) {
		return false;
	}

	protected boolean isTagaValue(String str) {
		return false;
	}

	protected void catchingCharactersEnded() {
		String name = (String) parsingElements.pop();
		if (isTagaKey(name)) {
			currentName = StringEscapeUtils.unescapeXml(currentBuff.toString())
					.trim();
		} else if (isTagaValue(name)) {
			currentValue = currentBuff.toString();
			currentValue = StringEscapeUtils.unescapeXml(currentValue).trim();
		}
	}

	protected void onePropertyEnded() {
		if (currentName != null && currentValue != null) {
			currentResource.putProperty(currentName, currentValue);
		}
		currentName = null;
		currentValue = null;
	}

	public void characters(char[] ch, int start, int length) {
		currentBuff.append(ch, start, length);
	}

	// for writing

	public String getSaveString(Collection resources) {
		StringBuffer buff = new StringBuffer();
		buff.append(startDocTag() + "\n");
		for (Iterator i = resources.iterator(); i.hasNext();) {
			NcbcResource element = (NcbcResource) i.next();
			buff.append(getSaveString(element, S4));
		}
		buff.append(endTag(getDocTag()));
		return buff.toString();
	}

	public abstract String getSaveString(NcbcResource rsc, String idt);

	public abstract String getDocTag();

	public String startDocTag() {
		return startTag(getDocTag());
	}

}
