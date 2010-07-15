package edu.ucla.loni.ccb.itools.parser;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;

import edu.ucla.loni.ccb.itools.DataBank;
import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

public class HtViewerDataHelper {
	static final Logger logger = Logger.getLogger(HtViewerDataHelper.class);
	public static final String TYPE = "type";
	public static final String URL = "url";
	public static final String ONTOLOGY = "ontology";
	public static final String CENTER = "center";

	public static final String TOOL = "tool";
	public static final String ITOOLS = "iTools";
	public static final String NAME = "name";
	public static final String NODE = "node";
	public static final String ITOOLS_URL = "http://www.loni.ucla.edu/twiki/bin/view/CCB/NCBC_ToolIntegration_XMLtology_2006";
	
	private static HashMap<String, Node> cachedOntology = new HashMap<String, Node>();
	
	public static String getAllResources4InteractiveViewer(boolean isSandBox, boolean centerCategorized) {
		Node root = new Node(ITOOLS);
		root.setAttribute(TYPE, "root");
		root.setAttribute(URL, ITOOLS_URL);
		
		if (centerCategorized) {			
			add4EachCenter(root, isSandBox);
		} else {
			addAllCenterTogether(root, isSandBox);
		}
		
		Element rootElement = node2Element(root);
		return JdomHelper.element2String(rootElement);
	}
	
    private static void addAllCenterTogether(Node root, boolean isSandBox) {
		List<NcbcResource> rscs = DataBank.getAllResources();
		addResources(root, rscs, isSandBox);
	}

	private static void add4EachCenter(Node root, boolean isSandBox) {
		for (NcbcCenter center : DataBank.getCenters()) {
			List<NcbcResource> rscs = null;
			if (center.getName().equals("NCBC")) {
				// this is not best, but  return all for NCBC for now
				rscs = DataBank.getAllResources();
			} else {
				rscs = DataBank.getResources(center.getName());
			}
			Node node = new Node(center.getName());
			node.setAttribute(TYPE, CENTER);
			root.addAsChild(node);
			addResources(node, rscs,isSandBox);
		}		
	}

	private static Element node2Element(Node node) {
		Element ele = new Element(NODE);
		ele.setAttribute(NAME, node.getName());
		String type = node.getAttribute(TYPE);
		if (type == null) {
			type = ONTOLOGY + node.getDepth();
		}		
		ele.setAttribute(TYPE, type);
		
		String url = node.getAttribute(URL);
		if (url != null) {
			ele.setAttribute(URL, url);
		}
		
		for (Node child : node.getChildren()) {
			Element ce = node2Element(child);
			int index = getIndex(child.getName(), ele);
//			logger.debug(node.getName() + " " + ele.getChildren().size()+", " + child.getName() +",but index of =" + getIndex(child.getName(), ce));
			ele.addContent(index, ce);
		}		
		return ele;		
	}


   private static void addOntology(Node ele) {
	   for (Node node : DataBank.getOntology() ) {
		   ele.addAsChild(new Node(node));		   
	   }
   }

	private static void addResources(Node ele, List<NcbcResource> resources, boolean forSand) {
		addOntology(ele);
		cachedOntology = Node.add2FlatCach(ele);
		
		for (NcbcResource rsc :  resources) {
			if (rsc.getName() == null) {
				logger.info("Name is not defined for this resources:"
						+ NcbcResourceParser.getSaveString(rsc));
				continue;
			}
			if (rsc.isInSandBox() == forSand) { // only add official resources.
				addResource(rsc);
			}
		}
	}
	
	private static Node createOntologyNode(String token, int level) {
		Node node = new Node(token);
		cachedOntology.put(token, node);
		node.setAttribute(TYPE, ONTOLOGY + level);
		return node;
	}
	private static void addResource(NcbcResource rsc) {
		String[] tokens = rsc.getOntology();

		for (String ontology : tokens) {
			String token = ontology.trim();
			Node node = cachedOntology.get(token);
			if (node == null) {
				node = createOntologyNode(token, 2);
				cachedOntology.get(Descriptor.NON_STANDARD).addAsChild(node);
			}
			add2Parent(node, rsc);
		}
	}
	
	
	private static void add2Parent(Node ele, NcbcResource rsc) {
        Node node = new Node(StringEscapeUtils.escapeXml(rsc.getName()));
        node.setAttribute(TYPE, TOOL);
		String url = rsc.getProperty(Descriptor.PROP_URL);
		if (url != null) {
			String[] strings = url.trim().split(" ");
			node.setAttribute(URL,  StringEscapeUtils.escapeXml(strings[0]));
		}
		ele.addAsChild(node);
	}
	


	private static int getIndex(String name, Element ele) {
		List<Element> children = JdomHelper.getChildren(ele);
		int rv = 0;
		for (Element child : children) {
			Attribute attribute = JdomHelper.getAttribute(child, NAME);
			if (attribute != null ) {
				String target = attribute.getValue();
				if (name.compareTo(target) <= 0 || shouldBeLast(target)) {
					return rv;
				} 
			}
			rv++;
		}
		return rv;
	}
	
	public static boolean shouldBeLast(String name) {
		return Descriptor.NON_STANDARD.equals(name) || Descriptor.NOVALUE.equals(name);
	}
	

	public static void main(String[] args) {
		Main.loadProperties(false);
		System.out.println(getAllResources4InteractiveViewer(false, false));
	}
}
