/* HTAction 
 */

package net.bouthier.hypertreeSwing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class HTAction extends MouseAdapter implements MouseMotionListener {
	private HTDraw model = null;

	private HTCoordE startPoint = null;

	private HTCoordE endPoint = null;

	private HTCoordS clickPoint = null;

	private boolean kleinMode = true;

	public HTDrawNode htDrawNode;

	HTAction(HTDraw model) {
		this.model = model;
		startPoint = new HTCoordE();
		endPoint = new HTCoordE();
		clickPoint = new HTCoordS();
	}

	public void mousePressed(MouseEvent e) {
		clickPoint.x = e.getX();
		clickPoint.y = e.getY();
		htDrawNode = model.findNode(clickPoint);

		if (e.isShiftDown())
			model.fastMode(true);
		if (e.isControlDown())
			model.longNameMode(true);
		startPoint.projectionStoE(e.getX(), e.getY(), model.getSOrigin(), model
				.getSMax());
	}

	public void mouseReleased(MouseEvent e) {
		model.fastMode(false);
		model.longNameMode(false);
		model.endTranslation();
	}

	public void mouseClicked(MouseEvent e) {
		if (e.isShiftDown())
			model.restore();
		else if (e.isControlDown())
			switchKleinMode();
		else {
			if (htDrawNode != null) {
				model.translateToOrigin(htDrawNode);
			}
		}
	}

	public void switchKleinMode() {
		if (kleinMode)
			kleinMode = false;
		else
			kleinMode = true;
		model.kleinMode(kleinMode);
	}

	public void mouseDragged(MouseEvent e) {
		if (startPoint.isValid()) {
			endPoint.projectionStoE(e.getX(), e.getY(), model.getSOrigin(),
					model.getSMax());
			if (endPoint.isValid())
				model.translate(startPoint, endPoint);
		}
	}

	public void mouseMoved(MouseEvent e) {
		/* empty */
	}
}