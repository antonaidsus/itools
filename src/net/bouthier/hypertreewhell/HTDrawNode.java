/* www.SOCR.ucla.edu
 * HyperTree Applet
 */

package net.bouthier.hypertreewhell;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Vector;

public class HTDrawNode
{
    private HTDraw model = null;
    private HTModelNode node = null;
    private HTCoordE ze = null;
    private HTCoordE oldZe = null;
    protected HTCoordS zs = null;
    private HTDrawNodeComposite father = null;
    protected HTNodeLabel label = null;
    protected boolean fastMode = false;
    protected boolean longNameMode = false;
    protected boolean kleinMode = false;
    protected HTDrawNode brother = null; /* protected for performance */
    
    /* totally new */
    private HTCoordE zy = null;
    private HTCoordE oldZy = null;
    /* totally new */
    
    HTDrawNode(HTDrawNodeComposite father, HTModelNode node, HTDraw model) {
	this.father = father;
	this.node = node;
	this.model = model;
	label = new HTNodeLabel(this);
	ze = new HTCoordE(node.getCoordinates());
	oldZe = new HTCoordE(ze);
	zs = new HTCoordS();
    }
    
    public HTModelNode getHTModelNode() {
	return node;
    }
    
    Color getColor() {
	return node.getNode().getColor();
    }
    
    public String getName() {
	return node.getName();
    }
    
    HTCoordE getCoordinates() {
	return ze;
    }
    
    HTCoordE getOldCoordinates() {
	return oldZe;
    }
    
    HTCoordS getScreenCoordinates() {
	return zs;
    }
    
    void refreshScreenCoordinates(HTCoordS sOrigin, HTCoordS sMax) {
	if (kleinMode)
	    zs.projectionEtoS(ze.pToK(model), sOrigin, sMax);
	else
	    zs.projectionEtoS(ze.p_zoom(model), sOrigin, sMax);
    }
//* start ----------------------------------------------------------------------
    HTDraw getModel() {
        return model;
    }
    
    HTDrawNodeComposite getFather() {
        return father;
    }
// end ---------------------------------------------------------------------//*/
    void drawBranches(Graphics g) {
	/* empty */
    }
    
    void drawNodes(Graphics g) {
	if (!fastMode)
	    label.draw(g);
    }
    
//    int getSpace() {
//	int dF = -1;
//	int dB = -1;
//	if (father != null) {
//	    HTCoordS zF = father.getScreenCoordinates();
//	    dF = zs.getDistance(zF);
//	}
//	if (brother != null) {
//	    HTCoordS zB = brother.getScreenCoordinates();
//	    dB = zs.getDistance(zB);
//	}
//	if (dF == -1 && dB == -1)
//	    return -1;
//	if (dF == -1)
//	    return dB;
//	if (dB == -1)
//	    return dF;
//	return Math.min(dF, dB);
//    }
    
    void specialTrans(HTCoordE alpha, HTCoordE beta) {
	ze.copy(oldZe);
	ze.specialTrans(alpha, beta);
        for (int index = 0; index < label.numOfLabelPoints; index++) {
            label.label_pts[index].copy(label.oldlabel_pts[index]);
            label.label_pts[index].specialTrans(alpha, beta);
        }
    }
    
    void endTranslation() {
	oldZe.copy(ze);
        for (int index = 0; index < label.numOfLabelPoints; index++) {
            label.oldlabel_pts[index].copy(label.label_pts[index]);
        }
    }
    
    void restore() {
	HTCoordE orig = node.getCoordinates();
	ze.x = orig.x;
	ze.y = orig.y;
	oldZe.copy(ze);
        computeLabelPoints();
    }
    
    void fastMode(boolean mode) {
	if (mode != fastMode)
	    fastMode = mode;
    }
    
    void longNameMode(boolean mode) {
	if (mode != longNameMode)
	    longNameMode = mode;
    }
    
    void kleinMode(boolean mode) {
	if (mode != kleinMode)
	    kleinMode = mode;
    }
    
    boolean getLongNameMode() {
	return longNameMode;
    }
    
    HTDrawNode findNode(HTCoordS zs) {
	if (label.contains(zs))
	    return this;
	return null;
    }
    
    public HTDrawNode findNode(String str) {
    	if (getName().toLowerCase().indexOf(str.toLowerCase())>=0)
    	    return this;
    	return null;
    }

    public void populateSearchVector(String str, java.util.Vector vec) {
    	if (getName().toLowerCase().indexOf(str.toLowerCase())>=0){
    		String nodeName=getName();
    		if (vec.size()==0) vec.addElement(this);
    		else if (!nodeName.equalsIgnoreCase(
    				((HTDrawNode)(vec.elementAt(vec.size()-1))).getName()))
    			vec.addElement(this);
    	}
    }
    
    public String toString() {
	String result = getName() + "\n\t" + ze + "\n\t" + zs;
	return result;
    }

    /*
      BEFORE trying to understand how computeLabelPoints() works, you need to
      READ the comment of the variable "numOfLabelPoints" in the file
      "HTDrawNode.java", otherwise, I gurentee your confusion :)
    */
    void computeLabelPoints() {
        HTCoordE ze = node.getCoordinates();

        /* 
          compute the angler
          adius_base is for the points: 4, 5
          angle_min is for the points: 1, 2
          angle_max is for the points: 0, 3
        */
        double angle_base = Math.atan2(ze.y, ze.x);
        double angle_min = angle_base - node.sector.getAngleQuota() / 2;
        double angle_max = angle_base + node.sector.getAngleQuota() / 2;

        /* 
          compute the radius
          radius_min is for the points: 0, 1
          radius_max is for the points: 2, 3
        */
        double radius_base = ze.d();
        double radius_adjust = (0.5/node.model.getTreeHeight());
        double radius_min = radius_base - radius_adjust;
        double radius_max;

//*/  // Temporary : Extend(or not) the leaf nodes
        radius_max = radius_base + radius_adjust;
/*/
        if (node.getNode().getNumOfChild() != 0)
            radius_max = radius_base + radius_adjust;// + .0755;
        else
            radius_max = 0.999;
//*/

        if (node.sector.getAngleQuota() == 2*Math.PI) {
            for (int i = 0; i < label.numOfLabelPoints; i++) {
                label.label_pts[i].x = Math.cos(i*Math.PI/4) * radius_max;
                label.label_pts[i].y = Math.sin(i*Math.PI/4) * radius_max;
            }
        }
        else {
//            {
            /* 
               It doesn't matter which point is computed first, for clear code,
               I choose to compute 4 corner points first, and then the
               Top and Bottom curves' points, and finally compute the
               Left and Right points
            */

            // compute the Top-Right point, that is, point 0
            label.label_pts[0].x = Math.cos(angle_min) * radius_max;
            label.label_pts[0].y = Math.sin(angle_min) * radius_max;

            // compute the Top-Left point, that is, point 2
            label.label_pts[2].x = Math.cos(angle_max) * radius_max;
            label.label_pts[2].y = Math.sin(angle_max) * radius_max;

            // compute the Bottom-left point, that is, point 4
            label.label_pts[4].x = Math.cos(angle_max) * radius_min;
            label.label_pts[4].y = Math.sin(angle_max) * radius_min;

            // compute the Bottom-Right point, that is, point 6
            label.label_pts[6].x = Math.cos(angle_min) * radius_min;
            label.label_pts[6].y = Math.sin(angle_min) * radius_min;

            /* compute the Top and Bottom control points */

            // compute the Top curve control point, that is, point 1
            label.label_pts[1].x = Math.cos(angle_base) * radius_max;
            label.label_pts[1].y = Math.sin(angle_base) * radius_max;

            // compute the Bottom curve control point, that is, point 5
            label.label_pts[5].x = Math.cos(angle_base) * radius_min;
            label.label_pts[5].y = Math.sin(angle_base) * radius_min;

            /* compute the Left and Right control points */

            // compute the Left curve control point, that is, point 3
            label.label_pts[3].x = (label.label_pts[2].x + label.label_pts[4].x)/2;
            label.label_pts[3].y = (label.label_pts[2].y + label.label_pts[4].y)/2;

            // compute the Right curve control point, that is, point 7
            label.label_pts[7].x = (label.label_pts[0].x + label.label_pts[6].x)/2;
            label.label_pts[7].y = (label.label_pts[0].y + label.label_pts[6].y)/2;
        }
        /* Record the positions */
        for (int index = 0; index < label.numOfLabelPoints; index++)
            label.oldlabel_pts[index].copy(label.label_pts[index]);
    }
        
    boolean isLeaf() {
	return true;
    }
    
    public Enumeration children() {
	/* empty */
        return new Vector(0).elements();
    }
}