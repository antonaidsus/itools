package net.bouthier.hypertreeSwing.applet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Controller;
import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.NcbcResourceParser;

/**
 * This class mainly get a xml format string which is all the available
 * resources used by HTPanel.
 * 
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class HTVieweDataHelper {
	private static final String VALUES = "VALUES";

	public static final Logger LOGGER = Logger
			.getLogger(HTVieweDataHelper.class);

	private static LinkedList<String[]> list = new LinkedList<String[]>();

	private static Map<String, Map<String, ?>> allMapsOfCenter;
	
	static String ontology_file = "/BiomedicalResourceOntology.txt";
	///CcbSoftwareOntology.txt

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

	/**
	 * @return a String which is an XML format of HT format.
	 */
	public static String getAllResources4InteractiveViewer() {
		return getAllResources4InteractiveViewer(false);
	}
	public static String getAllResources4InteractiveViewer(boolean isSandbox) {
		allMapsOfCenter = new HashMap<String, Map<String, ?>>(); // for single parent ontology

		StringBuffer bf = new StringBuffer(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		bf.append("<node name=\"iTools\" type=\"root\" url=\"http://www.loni.ucla.edu/twiki/bin/view/CCB/NCBC_ToolIntegration_XMLtology_2006\">\n");
		
		if (Controller.getInstance(isSandbox).isResourceCenterCategorized()) {			
			add4EachCenter(bf, isSandbox);
		} else {
			addAllCenterTogether(bf, isSandbox);
		}

		bf.append("</node>");
		return bf.toString();
	}

	private static void addAllCenterTogether(StringBuffer bf, boolean forSandbox) {
		List<NcbcResource> rscs = DaoFactory.getDaoFactory().getResourceDao().getAllResources();

		Map allResources = addResources2Map(rscs, forSandbox);
		append(bf, allResources, 0);
	}
	
	private static Map addResources2Map(List<NcbcResource> resources, boolean forSandbox) {
		Map map = new HashMap();
		addOntology2Map(map);
		for (NcbcResource rsc :  resources) {
			if (rsc.getName() == null) {
				LOGGER.info("Name is not defined for this resources:");
				LOGGER.info(NcbcResourceParser.getSaveString(rsc, ""));
				continue;
			}
			if (rsc.isInSandBox() == forSandbox) { // only add official resources.
				addResource2Map(map, rsc);
			}
		}
		return map;
	}
	
	private static void addResource2Map(Map map, NcbcResource rsc) {
		String[][] tokens = rsc.getOntology();
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].length == 1) {
				String token = tokens[i][0].trim();
				Map parent = (Map) allMapsOfCenter.get(token);
				if (parent == null) {// the parent is not a predefined
										// ontology node.
					parent = getChildMap(map, token);
				}
				add2Parent(parent, rsc);
			} else {
				Map parent = addOntology2Map(map, tokens[i], 0);
				add2Parent(parent, rsc);
			}
		}
	}
	
    private static void add4EachCenter(StringBuffer bf, boolean forSandbox) {
		Map<NcbcCenter, Map<String, ? extends Object>> externals = new HashMap<NcbcCenter, Map<String, ? extends Object>>();

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
			
			Map centerMap = addResources2Map(rscs, forSandbox);
			if (center.isExternal()) {
				// wait untill all external were found.
				externals.put(center, centerMap);
			} else {
				appendCenter2Buffer(bf, center, centerMap);
			}
		}

		// add externals such as iatr.
		if (externals.size() > 0) {
			bf.append("<node name=\"" + Descriptor.EXTERNAL_CENTER
					+ "\" type=\"center\" >\n");
			for (NcbcCenter center : externals.keySet()) {
				appendCenter2Buffer(bf, center, externals.get(center));
			}
			bf.append("</node>");
		}
	}

	private static Map<String, ?> getChildMap(Map<String, Map<String, ?>> parent, String token) {
		return getChildMap(parent, token, false);
	}
	
	private static Map getChildMap(Map<String, Map<String, ?>> parent, String token, boolean createIfNotyet) {
		Map<String, ?> tmp = parent.get(token);
		if (tmp == null) {
			tmp = new TreeMap();
			if (!createIfNotyet) {
				parent = (Map<String, Map<String, ?>>)allMapsOfCenter.get(Descriptor.NON_STANDARD);
			}
			parent.put(token, tmp);
			allMapsOfCenter.put(token, tmp);
		}
		return tmp;
	}

	
	private static void addOntology2Map(Map<String, Map<String, ?>> map) {
		for (Iterator<String[]> i = getOntology(); i.hasNext();) {
			addOntology2Map(map, i.next(), 0);
		}
	}

	private static Map addOntology2Map(Map<String, Map<String, ?>> map, String[] tokens, int index) {
		Map<String, Map<String, ?>> tmp = map;
		while (index < tokens.length) {
			tmp = getChildMap(tmp, tokens[index++].trim(), true);
		}
		return tmp;
	}

	private static void add2Parent(Map<String, List<NcbcResource>> map, NcbcResource rsc) {
		List<NcbcResource> list = map.get(VALUES);
		if (list == null) {
			list = new ArrayList<NcbcResource>();
			map.put(VALUES, list);
		}
		list.add(rsc);
	}

	public static Iterator<String[]> getOntology() {
		return list.iterator();
	}

	static Comparator<NcbcResource> resourceNameComparator = new Comparator<NcbcResource>() {
		public int compare(NcbcResource o1, NcbcResource o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	};

	private static void appendCenter2Buffer(StringBuffer bf, NcbcCenter center,
			Map<String, ?> centerMap) {
		bf.append("<node name=\"" + center.getName()
				+ "\" type=\"center\" url=\"" + center.getHomeUrl() + "\">\n");
		append(bf, centerMap, 0);
		bf.append("</node>");
	}

	@SuppressWarnings("unchecked")
	private static void append(StringBuffer bf, Map<String, ?> map, int level) {
		for (String element :  map.keySet()) {
			Object o = map.get(element);
			if (o instanceof Map) {
				bf.append("<node name=\""
						+ StringEscapeUtils.escapeXml(element)
						+ "\" type=\"ontology" + level + "\">\n");
				append(bf, (Map<String, ?>) o, level + 1);
				bf.append("</node>");
			} else {
				append(bf, (List<NcbcResource>) o);
			}
		}
	}

	private static void append(StringBuffer bf, List<NcbcResource> list) {
		Collections.sort(list, resourceNameComparator);
		for (NcbcResource rsc : list) {
			bf.append("<node name=\""
					+ StringEscapeUtils.escapeXml(rsc.getName())
					+ "\" type=\"tool\"");
			String url = rsc.getProperty(Descriptor.PROP_URL);
			if (url != null) {
				String[] strings = url.trim().split(" ");
				bf.append(" url=\"" + StringEscapeUtils.escapeXml(strings[0])
						+ "\"");
			}
			bf.append("/>\n");
		}
	}

}
