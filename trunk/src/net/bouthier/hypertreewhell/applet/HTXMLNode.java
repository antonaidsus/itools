/* www.SOCR.ucla.edu
 * HyperTree Applet
 */

package net.bouthier.hypertreewhell.applet;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Vector;

import net.bouthier.hypertreewhell.HTNode;

public class HTXMLNode implements HTNode
{
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
    
    public String getType() {
    	return type;
    }
    
    public void addChild(HTXMLNode child) {
	children.add(child);
    }
    
    public Enumeration children() {
	return children.elements();
    }
    
    public int getNumOfChild() {
        return children.size();
    }
    
    public int getNumberOfDecends(String type) {
    	int rv = 0;
    	for (int i =0, I = children.size(); i < I; i++) {
    		rv +=  ((HTXMLNode)children.get(i)).getNumberOfDecends(type);
    	}
    	if (type.equals(this.type)) {
    		rv++;
    	}
    	return rv;
    }
        
    public boolean isLeaf() {
//	return false;
       return children.size() == 0 ? true : false;
    }
    
    public String getName() {
	return name;
    }
    
    public Color getColor() {
	if (type == null)
	    return Color.lightGray;
	if (type.equals("root"))
	    return Color.magenta;
	if (type.equals("tools"))
	    return Color.white;
	if (type.equals("materials"))
	    return Color.green;
	if (type.equals("wiki"))
	    return Color.cyan;
	if (type.indexOf("ontology")>=0) {
		Color ontologyLevelColor;
		// Get ontology-level
		int ontologyLevel = Integer.parseInt(type.substring(type.indexOf("ontology")+8));
		ontologyLevel = ontologyLevel%10;	// Module 10, as only ten levels of colors are defined
		
		// Assign appropriate color for this level.
		switch (ontologyLevel) {
			case 0: ontologyLevelColor = new Color(102, 255, 255);
			case 1: ontologyLevelColor = new Color(102, 255, 153);
			case 2: ontologyLevelColor = new Color(102, 255, 51);
			case 3: ontologyLevelColor = new Color(102, 204, 255);
			case 4: ontologyLevelColor = new Color(102, 204, 153);
			case 5: ontologyLevelColor = new Color(102, 204, 51);
			case 6: ontologyLevelColor = new Color(102, 153, 255);
			case 7: ontologyLevelColor = new Color(102, 153, 153);
			case 8: ontologyLevelColor = new Color(102, 153, 51);
			case 9: ontologyLevelColor = new Color(102, 51, 255);
			default: ontologyLevelColor = Color.GRAY;
		}
		return ontologyLevelColor;
	}
	return Color.gray;
    }
}