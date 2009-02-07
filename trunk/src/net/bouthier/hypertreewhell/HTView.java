/* www.SOCR.ucla.edu
 * HyperTree Applet
 */

package net.bouthier.hypertreewhell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import net.bouthier.hypertreewhell.applet.HTXMLNode;

public class HTView extends JPanel {
	public HTModel model = null;
	public HTDraw draw = null;
	public HTAction action = null;
	public boolean isDrawLine = false; // it won't draw branch at the beginning

	public HTView(HTModel model) {
		super(new BorderLayout());
		setPreferredSize(new Dimension(250, 250));
		setBackground(Color.WHITE);
		this.model = model;
		draw = new HTDraw(model, this);
		action = new HTAction(draw);
		startMouseListening();
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	public Object getNodeUnderTheMouse(MouseEvent event) {
		HTDrawNode node = getDrawNode(event);
		if (node != null)
			return node.getHTModelNode().getNode();
		return null;
	}

	public HTDrawNode getDrawNode(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		return draw.findNode(new HTCoordS(x, y));
	}
	
    String TOOL = "tool";
	public String getToolTipText(MouseEvent event) {
		String tip = null;

		HTDrawNode node = getDrawNode(event);
		if (node != null) {
			tip = node.getName();
			HTNode node2 = node.getHTModelNode().getNode();
			if (node2 instanceof HTXMLNode) {
				HTXMLNode node3 = (HTXMLNode)node2;
				if (!node3.getType().equals(TOOL)) {
				    tip = tip + " has " + node3.getNumberOfDecends(TOOL) + " resources";
				} else {
					tip = tip + " This is a resource";
				}
			}
		}
		return tip;
	}
	

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw.refreshScreenCoordinates();
		draw.drawNodes(g);
		if (isDrawLine)
			draw.drawBranches(g);
	}

	void stopMouseListening() {
		removeMouseListener(action);
		removeMouseMotionListener(action);
		removeMouseWheelListener(action);
	}

	void startMouseListening() {
		addMouseListener(action);
		addMouseMotionListener(action);
		addMouseWheelListener(action);
	}
}