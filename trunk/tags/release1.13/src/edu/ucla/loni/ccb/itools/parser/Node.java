/**
 * 
 */
package edu.ucla.loni.ccb.itools.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Node {
	private Node parent;
	private String name;
	private HashMap<String, String> attributes = new HashMap<String, String>();
	private LinkedList<Node> children = new LinkedList<Node>();
	private int depth = 0;

	public Node(String name) {
		this.name = name;
	}

	public Node(Node copy) {
		this.name = copy.name;
		attributes = new HashMap<String, String>(copy.attributes);
		for (Node child : copy.children) {
			Node node = new Node(child);
			node.parent = this;
			children.add(node);
		}
	}

	public void addAsChild(Node node) {
		children.add(node);
		node.parent = this;
	}

	public List<Node> getChildren() {
		return children;
	}

	public String getAttribute(String name) {
		return attributes.get(name);
	}

	public String setAttribute(String name, String value) {
		return attributes.put(name, value);
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public static HashMap<String, Node> add2FlatCach(Node node) {
		HashMap<String, Node> rv = new HashMap<String, Node>();
		node.depth = 0;
		add2FlatCach(rv, node);
		return rv;
	}

	public static void add2FlatCach(HashMap<String, Node> map, Node node) {
		map.put(node.name, node);
		
		for (Node child : node.children) {
			child.depth = node.depth + 1;
			add2FlatCach(map, child);
		}
	}

	public String getName() {
		return name;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getDepth() {
		return depth;
	}

}