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

public class BiomedicalRscOntologyParser extends JdomHelper {
	static final Logger logger = Logger.getLogger(BiomedicalRscOntologyParser.class);

	private static final String RESOURCE = "resource";
	private static final String ABOUT = "about";
	public static final String NAME_CLASS = "Class";
	public static final String NAME_SUB_CLASS_OF = "subClassOf";
	public static final char POND_SIGN = '#';
	public static final String DATATYPE_PROPERTY = "DatatypeProperty";
	
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
	
	private Node getNode(String name) {
		Node node = allNodes.get(name);
		if (node == null) {
			node = new Node();
			node.name = name;
			int i = name.lastIndexOf(POND_SIGN);
			node.displayLabel = name.substring(i+1);
			allNodes.put(name, node);
			finalSetOfNodes.add(node);
		}				
        return node;
	}
	
	//deals with Element which is <owl:Class>
	private Node element2Node(Element e) {
		Node node = null;	
		if (DATATYPE_PROPERTY.equals(e.getName())) {
			Attribute a = getAttribute(e,ABOUT);
			if (a != null) {
				String v = a.getValue();
				int i = v.lastIndexOf(POND_SIGN);
				if (i != -1) {
					v = v.substring(i+1);
				}
				infoModel.add(v);
			}
		}
		if (!NAME_CLASS.equals(e.getName())) {
			return node;
		}

		List<Attribute> attributes = getAttributes(e);
		for (Attribute a : attributes) {
			if (a.getName().equals(ABOUT)) {
				String name = a.getValue();
				node = getNode(name);
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
				    Node  tmp = element2Node(grandChild);
				    if (tmp != null) {
				        tmp.addAsChild(node);
				        finalSetOfNodes.remove(node);
				    }
				}
			}
		}
		return node;
	}

	private static void node2String(Node node, StringBuilder buf, String str) {
		str = str + (str==""? "" : "/") + node.displayLabel;
		if (node.children.size() == 0) {
			buf.append( str + "\n");
		} else {
		    for (Node child : node.children) {
			    node2String(child, buf, str);
		    }
		}
	}

	public static void main(String[] args) {
		try {
			BiomedicalRscOntologyParser inst = new BiomedicalRscOntologyParser();
			inst.parse(
					new FileInputStream("resources/metadata/BiomedicalResourceOntology.xml"));
			System.out.println(inst.allNodes.size() + ", final = " + inst.finalSetOfNodes.size());
			StringBuilder buf = new StringBuilder();
			for (Node node : inst.finalSetOfNodes) {
				node2String(node, buf, "");
//				System.out.println(node.displayLabel);
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
}
