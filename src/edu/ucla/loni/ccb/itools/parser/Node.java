/**
 * 
 */
package edu.ucla.loni.ccb.itools.parser;

import java.util.LinkedList;

class Node {
	String parentName;
	LinkedList<Node> children = new LinkedList<Node>();
	String name="";
	String displayLabel;

	Node(){   		
	}
	void addAsChild(Node node) {
		children.add(node);
	}
}