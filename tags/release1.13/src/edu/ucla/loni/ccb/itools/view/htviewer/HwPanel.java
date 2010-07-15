/* www.SOCR.ucla.edu
 * HyperTree Applet
 */

package edu.ucla.loni.ccb.itools.view.htviewer;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.ucla.loni.ccb.gui.htviewer.HtView;
import edu.ucla.loni.ccb.gui.htviewer.hw.HwView;

public class HwPanel extends HtPanel  {
	private static final long serialVersionUID = 1L;

	public JButton drawlinesButton;	
	private ImageIcon drawlines;

	public HwPanel(boolean forSandbox) {
		super(forSandbox);
		drawlinesButton = addButton("", drawlines,"Turn On/Off Lines", "drawline", this);
	}

	public HwPanel() {
		this(false);
	}

	protected HtView createView() {
		HtView tmp = new HwView();
		tmp.addMouseListener(new HtViewerMouseListener());
		return tmp;
	}

	protected void createIcons() {
		homeIcon = getIcon("go-home.png");
		backIcon = getIcon("go-previous.png");
		dimmedBackIcon = getIcon("go-previous.png");
		forwardIcon = getIcon("go-next.png");
		dimmedForwardIcon = getIcon("go-next.png");
		searchIcon = getIcon("system-search.png");
		infoIcon = getIcon("help-browser.png");
		displayModelIcon = getIcon("system-shutdown.png");
		drawlines = getIcon("view-fullscreen.png");
	}
	// User clicked one of the action buttons on the top
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("drawline")) {
			view.setDrawLine(!view.isDrawLine());
			view.repaint();
		} else  {
			super.actionPerformed(e);
		}
	}
	
	public String getViewType() {
		return "HyperWheel Viewer";
	}
	
}