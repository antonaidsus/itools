package edu.ucla.loni.ccb.itools.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.Util;

/**
 * This class parse the owl OnTology After invoke parse() or load(), call
 * getFinalSetOfNodes() to get the list of Nodes.
 * 
 * @author jeff.ma
 * 
 */
public class BiomedicalRscOntologyParser extends JdomHelper {
	static final Logger logger = Logger
			.getLogger(BiomedicalRscOntologyParser.class);

	private static final String RESOURCE = "resource";
	private static final String ABOUT = "about";
	public static final String NAME_CLASS = "Class";
	public static final String NAME_SUB_CLASS_OF = "subClassOf";
	public static final char POND_SIGN = '#';
	public static final String DATATYPE_PROPERTY = "DatatypeProperty";
	public static final String OBJECT_PROPERTY = "ObjectProperty";
	
	public static final String DESCRIPTION = "Description";
	public static final String TYPE = "type";
	public static final String REPLACEDBY="replacedBy";
	public static final String DEPRECATEDCLASS="DeprecatedClass";

	private static final HashMap<String, ArrayList<String>> deprecatedOntologies = new HashMap<String, ArrayList<String>>() ;

	private HashMap<String, Node> allNodes = new HashMap<String, Node>();
	private List<Node> finalSetOfNodes = new ArrayList<Node>();
	private List<String> infoModel = new ArrayList<String>();

	public void parse(InputStream input) {
		try {
			Document document = buildDocument(new InputStreamReader(input));
			List<Element> children = getChildren(document.getRootElement());
			for (Element e : children) {
				element2Node(e);
			}
		} catch (Exception e) {
			logger.warn("parse error", e);
		}
	}

	/**
	 * As of 09/20/2009, with new schema only DESCRIPTION is true.
	 * @param e
	 * @return
	 */
	private Node element2Node(Element e) {
		Node node = null;
		if (DATATYPE_PROPERTY.equals(e.getName())) {
			datatypePropertyElement(e);
		} else if (OBJECT_PROPERTY.equals(e.getName())) {
			objectPropertyElement(e);
		} else if (NAME_CLASS.equals(e.getName())) {
			node = owlClassElement(e);
		} else if (DESCRIPTION.equals(e.getName())) {
			node = descriptionElement(e);
		} 
		return node;
	}
	
	/**
	 * OnTology owl changed their format, now every definition seems start with
	 * "Description" type. 09/20/2009
	 * @param e a Description tag
	 * @return
	 */
	private Node descriptionElement(Element e) {
		if (isOntologyType(e)) {
			return descriptionClassElement(e);
		} else if (isDatatypeProperty(e)) {
			datatypePropertyElement(e);
		}
		return null;
	}
	
	private static boolean isOntologyType(Element e) {
		boolean rv = false;
				
		String name = getAttrValue(e, ABOUT);
		if (name == null) {
			return false;
		}

		List<Element> types = getChildren(e, TYPE);
		for (Element child : types) {
			String attrValue = getAttrValue(child, RESOURCE);
			if (attrValue.equals(NAME_CLASS)) {
				rv = true;
				break;
			}
		}
		
		if (rv) {
			for (Element child : types) {
				String attrValue = getAttrValue(child, RESOURCE);
				if (attrValue.equals(DEPRECATEDCLASS)) {
					rv = false;
					break;
				}
			}
			
			if (!rv) {
				List<Element> replaceBys = getChildren(e, REPLACEDBY);
				if (replaceBys.size() > 0) {
					ArrayList<String> replaces = new ArrayList<String>(
							replaceBys.size());
					for (Element replace : replaceBys) {
						String resourceAttr = getResourceAttr(replace);
						replaces.add(resourceAttr);
					}
					deprecatedOntologies.put(name, replaces);
				}
			}
		}
		return rv;
	}
		
	private static boolean isDatatypeProperty(Element e) {
		boolean rv = false;
		Element child = getChild(e, TYPE);
		if (child != null) {
			rv = getAttrValue(child, RESOURCE).equals(DATATYPE_PROPERTY);
		}
		
		return rv;
	}
	
	private Node getNode(String name) {
		int i = name.lastIndexOf(POND_SIGN);
		name = name.substring(i + 1);

		Node node = allNodes.get(name);
		if (node == null) {
			node = new Node(name);
			allNodes.put(name, node);
			finalSetOfNodes.add(node);
		}
		return node;
	}
	
	private static String getResourceAttr(Element e) {
		String rv = "NO_RESOURCE_ATTR_DEFINED";
		String attrValue = getAttrValue(e, RESOURCE);
		return attrValue != null ? attrValue : rv;		
	}
	
	private static String getAttrValue(Element e, String attrName) {
		String rv = null;
		List<Attribute> attributes = getAttributes(e);
		for (Attribute a : attributes) {
			if (a.getName().equals(attrName)) {
				rv = a.getValue();
				int i = rv.lastIndexOf(POND_SIGN);
				rv = rv.substring(i + 1);
				break;
			}
		}
        return rv;
	}
	

	// deals with Element which is <owl:Class>
	// owl:class seems has two way to define parent/child structure
	// has "subClassOf" child element
	// 1) with "resource" attribute
	// 2) owl:class element
	private Node owlClassElement(Element e) {
		Node node = null;
		List<Attribute> attributes = getAttributes(e);
		for (Attribute a : attributes) {
			if (a.getName().equals(ABOUT)) {
				String name = a.getValue();
				node = getNode(name); // creates if not yet.
				break;
			}
		}

		List<Element> children = getChildren(e);
		for (Element child : children) {
			if (NAME_SUB_CLASS_OF.equals(child.getName())) {
				attributes = getAttributes(child);
				for (Attribute a : attributes) {
					if (a.getName().equals(RESOURCE)) {
						Node tmp = getNode(a.getValue());
						tmp.addAsChild(node);
						finalSetOfNodes.remove(node);
					}
				}
				for (Element grandChild : getChildren(child)) {
					Node tmp = element2Node(grandChild);
					if (tmp != null) {
						tmp.addAsChild(node);
						finalSetOfNodes.remove(node);
					}
				}
			}
		}
		return node;
	}
	
	private Node descriptionClassElement(Element e) {
		String name = getAttrValue(e, ABOUT);
		if (name == null) {
			return null;
		}
		Node node = getNode(name); // creates if not yet.

		List<Element> children = getChildren(e, NAME_SUB_CLASS_OF);
		for (Element child : children) {
			String resourceName = getAttrValue(child, RESOURCE);
			Node tmp = getNode(resourceName);
			tmp.addAsChild(node);
			finalSetOfNodes.remove(node);
		}
		return node;
	}

	private void objectPropertyElement(Element e) {

	}

	private void datatypePropertyElement(Element e) {
		String name = getAttrValue(e, ABOUT);
		if (name != null)
		{
			infoModel.add(name);
		}
	}

	private static void node2String(Node node, StringBuilder buf, String str) {
		str = str + (str == "" ? "" : "/") + node.getName();
		if (node.getChildren().size() == 0) {
			buf.append(str + "\n");
		} else {
			for (Node child : node.getChildren()) {
				node2String(child, buf, str);
			}
		}
	}

	public List<Node> load() {
		return load(BiomedicalRscOntologyParser.class
				.getResourceAsStream("/BiomedicalResourceOntology.xml"));
	}

	public List<Node> load(InputStream input) {
		try {
			parse(input);

			// add Descriptor.NON_STANDARD, Descriptor.NOVALUE
			Node node = new Node(Descriptor.NON_STANDARD);
			Node node2 = new Node(Descriptor.NOVALUE);

			node.addAsChild(node2);
			finalSetOfNodes.add(node);
		} catch (Exception e) {
			logger.warn("parse BiomedicalResourceOntology.xml", e);
		}
		return finalSetOfNodes;
	}

	public static void main(String[] args) {
		try {
			BiomedicalRscOntologyParser inst = new BiomedicalRscOntologyParser();
			inst.parse(new FileInputStream(
					"resources/metadata/BiomedicalResourceOntology.xml"));
			System.out.println(inst.allNodes.size() + ", final = "
					+ inst.finalSetOfNodes.size());
			StringBuilder buf = new StringBuilder();
			for (Node node : inst.finalSetOfNodes) {
				node2String(node, buf, "");
				// System.out.println(node.displayLabel);
			}
			buf.append("\nHere is the deprecated ontology\n");
			for (String key : deprecatedOntologies.keySet()) {
				ArrayList<String> v = deprecatedOntologies.get(key);
				buf.append("\n" + key + "-->" + Util.toString(v.toArray(new String[v.size()])));				
			}
			
			System.out.println(buf);
			System.out.println("Here is the information Model");
			for (String v : inst.infoModel) {
				System.out.println(v);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public HashMap<String, Node> getAllNodes() {
		return allNodes;
	}

	public List<Node> getFinalSetOfNodes() {
		return finalSetOfNodes;
	}

	public List<String> getInfoModel() {
		return infoModel;
	}
	
	public HashMap<String, ArrayList<String>> getDeprecatedOntologies() {
		return deprecatedOntologies;
	}
}
