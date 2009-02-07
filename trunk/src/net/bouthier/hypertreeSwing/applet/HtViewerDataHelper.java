package net.bouthier.hypertreeSwing.applet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;

import edu.ucla.loni.ccb.itools.Controller;
import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.JdomHelper;
import edu.ucla.loni.ccb.itools.parser.NcbcResourceParser;

public class HtViewerDataHelper {
	private static final String TYPE = "type";
	private static final String URL = "url";

	public static final Logger LOGGER = Logger
	.getLogger(HtViewerDataHelper.class);

	private static final String ITOOLS = "iTools";
	private static final String NAME = "name";
	private static final String NODE = "node";
	static String ontology_file = "/BiomedicalResourceOntology.txt";
	///CcbSoftwareOntology.txt
	private static LinkedList<String[]> list = new LinkedList<String[]>();
	private static HashMap<String, Element> cashedMap = new HashMap<String, Element>();

	static {
		LOGGER.debug(" start loading Ontology from classpath: " + ontology_file);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				HTVieweDataHelper.class
						.getResourceAsStream(ontology_file)));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(Descriptor.DELIMITER);
				list.add(tokens);
			}
	        list.add(new String[]{Descriptor.NON_STANDARD, Descriptor.NOVALUE});
		} catch (Exception e) {
			LOGGER.error("Read ontology error");
		}
		LOGGER.debug("Ontology was loaded from classpath");
	}

	public static String getAllResources4InteractiveViewer(boolean isSandBox) {
        Element root = new Element(NODE);
        Attribute a= new Attribute(NAME, ITOOLS);
        root.setAttribute(a);
        root.setAttribute(TYPE, "root");
        root.setAttribute(URL, "http://www.loni.ucla.edu/twiki/bin/view/CCB/NCBC_ToolIntegration_XMLtology_2006");
		
		if (Controller.getInstance(isSandBox).isResourceCenterCategorized()) {			
			add4EachCenter(root);
		} else {
			addAllCenterTogether(root);
		}
 
		return JdomHelper.element2String(root);
	}
	
    private static void add4EachCenter(Element ele) {
		for (NcbcCenter center : DaoFactory.getDaoFactory().getCenterDao().getCenters()) {
			List<NcbcResource> rscs = null;
			if (center.getName().equals("NCBC")) {
				// this is not best, but  return all for NCBC for now
				rscs = DaoFactory.getDaoFactory().getResourceDao()
						.getAllResources();
			} else {
				rscs = DaoFactory.getDaoFactory().getResourceDao().load(
						center.getName());
			}
			String name = center.getName() + (center.isExternal() ? "(External)" : "");
			
			Element e = new Element(NODE);
			e.setAttribute(NAME, name);
			e.setAttribute(TYPE, "center");
			ele.addContent(e);
			addResources(e, rscs);
		}
	}

	private static void addAllCenterTogether(Element ele) {
		List<NcbcResource> rscs = DaoFactory.getDaoFactory().getResourceDao().getAllResources();
		addResources(ele, rscs);
	}

	private static void addResources(Element ele, List<NcbcResource> resources) {
		addOntology(ele);
		for (NcbcResource rsc :  resources) {
			if (rsc.getName() == null) {
				LOGGER.info("Name is not defined for this resources:");
				LOGGER.info(NcbcResourceParser.getSaveString(rsc, ""));
				continue;
			}
			if (!rsc.isInSandBox()) { // only add official resources.
				addResource(ele, rsc);
			}
		}
	}

	private static void addOntology(Element ele) {
		for (Iterator<String[]> i = getOntology(); i.hasNext();) {
			addOntology(ele, i.next(), 0);
		}
	}

	private static void addOntology(Element ele, String[] tokens, int index) {
		while (index < tokens.length) {
			ele = createChild(ele, tokens[index++].trim());
		}
	}
	
	private static Element createChild(Element ele, String token) {
		Element element = new Element(token.replaceAll(" ", "_"));
		ele.addContent(element);
		cashedMap.put(token, element);
		return element;
	}
		
	public static Iterator<String[]> getOntology() {
		return list.iterator();
	}

	static Comparator<NcbcResource> resourceNameComparator = new Comparator<NcbcResource>() {
		public int compare(NcbcResource o1, NcbcResource o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	};

	private static void add2Parent(Element ele, NcbcResource rsc) {
		Element e = new Element(NODE);
		e.setAttribute(TYPE, "tool");
		e.setAttribute(NAME, StringEscapeUtils.escapeXml(rsc.getName()));
		String url = rsc.getProperty(Descriptor.PROP_URL);
		if (url != null) {
			String[] strings = url.trim().split(" ");
			e.setAttribute(URL,  StringEscapeUtils.escapeXml(strings[0]));
		}
		ele.addContent(e);
	}
	
	private static void addResource(Element ele, NcbcResource rsc) {
		String[][] tokens = rsc.getOntology();
		for (String[] ontology : tokens) {
			String token = ontology[ontology.length -1].trim();
			Element element = cashedMap.get(token);
			if (element == null) {
				element = createChild(cashedMap.get(Descriptor.NON_STANDARD), token);
			}
			add2Parent(element, rsc);
		}
	}

	public static void main(String[] args) {
		System.out.println(getAllResources4InteractiveViewer(false));
	}

}
