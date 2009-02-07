package edu.ucla.loni.ccb.itools.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * This class used to parse the CCB Software Ontology file
 * to create the tree structure.
 * @author Qunfei Ma
 *
 */
public class CcbSoftwareOntologyparser extends MyXmlParser {
	/** Logger for this class. */
    public static final Logger LOGGER = Logger.getLogger(CcbSoftwareOntologyparser.class);
    /** Used to report line number. */
    private Stack parsingElements = new Stack();
    private Node currentNode;
    private StringBuffer namebuff = new StringBuffer();
    private StringBuffer superclassbuff = new StringBuffer();
    Set allnodes = new HashSet();
    
    public static void main(String[] args) {
    	try {
			parse(new FileInputStream("resources/metadata/CcbSoftwareOntology.xml"));
		} catch (FileNotFoundException e) {
			LOGGER.warn(e,e);
		}
    }
    
	public static void parse(InputStream input) {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(input, new CcbSoftwareOntologyparser());
		} catch (Exception e) {
			LOGGER.warn(e,e);
		}
	}

    public void endDocument() {
    	Iterator i = allnodes.iterator();
    	try {
			FileWriter fw = new FileWriter("resources/metadata/CcbSoftwareOntology.txt");
			while(i.hasNext()) {
				Node node = (Node) i.next();
				fw.write(getString(node));
				fw.write('\n');
			}
			fw.close();
		} catch (IOException e) {
			LOGGER.warn(e,e);
		} 
    }
    
    private String getString(Node node) {
    	String str = node.name;
    	while ((node = getParent(node)) != null) {
    		str = node.name + "/" +str;
    	}
    	return str;
    }
    
    private Node getParent(Node node) {
    	Node n = null;
    	String name = node.parentName;
    	if (name == null ||name.startsWith(":")) return null;
    	
    	Iterator i = allnodes.iterator();    	
    	while(i.hasNext()) {
    		 n = (Node) i.next();
    		 if (n.name.equals(name)) return n;
    	}
    	return null;
    }
    
    public void endElement(String uri, String localName, String qName) {
		Object object = parsingElements.pop();
		if (!qName.equals(object)) {
			throw new RuntimeException(" node not formated");
		}
    	if (qName.equals("class")) {
    		
    	} else  if (qName.equals("name")) {
        	object = parsingElements.peek();
        	if (object.equals("class")) {
        		currentNode.name = namebuff.toString();
        		namebuff = new StringBuffer();
        	}       	
        } else if (qName.equals("superclass")) {
        	String supname = superclassbuff.toString();
        	currentNode.parentName = supname;
        	superclassbuff = new StringBuffer();
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
    	parsingElements.push(qName);
    	if (qName.equals("class")) {
    		if (currentNode != null && !currentNode.name.startsWith(":")) {
    			allnodes.add(currentNode);
    		}
    		currentNode = new Node();
    	} else if (qName.equals("superclass")) {
        
        } else {
        	
        }
    }
    
    public void characters(char[] ch, int start, int length) {
        if (length <= 1) return;
        Object object = parsingElements.peek();
        if (object.equals("name")) {
        	namebuff.append(ch, start, length);
        } else if (object.equals("superclass")) {
        	superclassbuff.append(ch, start, length);
        }
    }
}
