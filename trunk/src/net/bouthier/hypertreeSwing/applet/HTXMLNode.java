/* www.SOCR.ucla.edu
 * HyperTree Applet
 */

package net.bouthier.hypertreeSwing.applet;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Vector;

import net.bouthier.hypertreeSwing.HTNode;

public class HTXMLNode implements HTNode {
	private String name = null;
	private String type = null;
	private String url = null;
	private Vector children = null;

	public HTXMLNode(String name, String type, String url) {
		children = new Vector();
		this.name = name;
		this.type = type;
		this.url = url;
	}

	public String getURL() {
		return url;
	}

	public void addChild(HTXMLNode child) {
		children.add(child);
	}

	public Enumeration children() {
		return children.elements();
	}

	public boolean isLeaf() {
		return false;
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		if (type == null)
			return Color.lightGray;
		if (type.equals("root"))
			return Color.magenta;
		if (type.equals("tool"))
			return Color.green;
		if (type.equals("center"))
			return Color.cyan;
		if (type.indexOf("ontology") >= 0) {
			// Get ontology-level
			int ontologyLevel = Integer.parseInt(type.substring(type
					.indexOf("ontology") + 8));
			ontologyLevel = ontologyLevel % 10; // Module 10, as only ten levels
												// of colors are defined
			// Assign appropriate color for this level.
			int com = 150 + ontologyLevel * 25;
			if (com > 250)
				com = 250;
			return new Color(com, com, com);
			// switch (ontologyLevel) {
			// case 0: ontologyLevelColor = new Color(102, 255, 255); break;
			// case 1: ontologyLevelColor = new Color(102, 255, 153); break;
			// case 2: ontologyLevelColor = new Color(102, 255, 51); break;
			// case 3: ontologyLevelColor = new Color(102, 204, 255);break;
			// case 4: ontologyLevelColor = new Color(102, 204, 153);break;
			// case 5: ontologyLevelColor = new Color(102, 204, 51);break;
			// case 6: ontologyLevelColor = new Color(102, 153, 255);break;
			// case 7: ontologyLevelColor = new Color(102, 153, 153);break;
			// case 8: ontologyLevelColor = new Color(102, 153, 51);break;
			// case 9: ontologyLevelColor = new Color(102, 51, 255);break;
			// default: ontologyLevelColor = Color.GRAY;break;
			// }
			// return ontologyLevelColor;
		}
		return Color.gray;
	}
}