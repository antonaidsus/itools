/* www.SOCR.ucla.edu
 * HyperTree Applet
 */

package edu.ucla.loni.ccb.itools.view.htviewer;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.StringReader;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.gui.htviewer.BasePanel;
import edu.ucla.loni.ccb.gui.htviewer.Controller;
import edu.ucla.loni.ccb.gui.htviewer.HtNode;
import edu.ucla.loni.ccb.gui.htviewer.HtView;
import edu.ucla.loni.ccb.gui.htviewer.HtXMLNode;
import edu.ucla.loni.ccb.gui.htviewer.HtXMLReader;
import edu.ucla.loni.ccb.itools.parser.HtViewerDataHelper;
import edu.ucla.loni.ccb.itools.view.MainFrame;

public class HtPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	public static final String SHOW_CENTER = "Show Centers";
	public static final String NO_CENTER = "No Centers";
	public static final String SHOW_CENTER_TIP = "toogle centers info show/off in the view";
	public static final String SHOW_CENTER_CMD = "toogleCenter";

	private static final Logger logger = Logger.getLogger(HtPanel.class);
	protected JButton toggleCenterButton;
	private HtXMLNode rootNodeWithCenter;
	private HtXMLNode rootNodeWithoutCenter;
	protected boolean isSandBox;

	public HtPanel() {
		this(false);
	}

	public HtPanel(boolean isSandBox) {
		this.isSandBox = isSandBox;
		toggleCenterButton = addButton(SHOW_CENTER, null, SHOW_CENTER_TIP,
				SHOW_CENTER_CMD, this);
		Controller.getInstance(isSandBox).addPropertyChangeListener(this);
	}
	
	public void init() {
		String str = HtViewerDataHelper.getAllResources4InteractiveViewer(isSandBox, true);
		setDataWithCenter(str);
		str = HtViewerDataHelper.getAllResources4InteractiveViewer(isSandBox, false);
		setDataWithoutCenter(str);
	}

	protected void postNodeSelected(HtNode node) {
		String str =  node2RootPath(node);
		setFiringChangeEvent(true);
		Controller.getInstance(isSandBox).firePropertyChange(
				new PropertyChangeEvent(this, Controller.SELECTED_PATH, null,
						str.split(DELIMITER)));
		setFiringChangeEvent(false);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == toggleCenterButton) {
			doActionToggleCenter();
		} else {
			super.actionPerformed(e);
		}
	}

	protected void doActionInfo() {
		try {
			JOptionPane
					.showMessageDialog(
							this,
							getApplicationName() + " " + getViewType()
									+ "\n\n"
									+ "You can traverse and explore most of the tools, libraries, etc. by navigating\n"
									+ " the HT Graph or by using keyword searches.\n\n"
									+ "You can click and drag on a node to refocus the display.\n"
									+ "Double-clicking on any node will display the chosen iTools resource in a new window.\n\n"
									+ "To see the FULL Node Resource Names: either bring mouse over a Node or \n"
									+ "hold down CONTROL key and press the mouse.\n",
							"                iTools HT Information Dialog",
							JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception except) {
			JOptionPane.showMessageDialog(this, except.getMessage());
			except.printStackTrace();
		}
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getSource() == this || isFiringChangeEvent()) {
			return;
		}

		String propertyName = arg0.getPropertyName();
		logger.debug(getClass().getName() + " PropertyEvent received: source="
				+ arg0.getSource() + ", property=" + propertyName + ", nvalue="
				+ toString(arg0.getNewValue()));

		if (propertyName.startsWith(Controller.DATA_OF)) {
			//the resource was deleted, need reload, cause it may have 1+ ontology.
			//or a new resource is added
			//or the center was updated.

			init();
		} else if (propertyName.startsWith(Controller.CENTER_CATEGORIZED)) {
			if (Controller.getInstance(isSandBox).isCenterCategorized()) {
				setData(rootNodeWithCenter);
				toggleCenterButton.setText(NO_CENTER);
			} else {
				setData(rootNodeWithoutCenter);
				toggleCenterButton.setText(SHOW_CENTER);
			}
		} else {
			super.propertyChange(arg0);
		}
	}

	protected HtView createView() {
		view = new HtView();
		view.addMouseListener(new HtViewerMouseListener());
		return view;
	}

	public String getViewType() {
		return "HyperTree Viewer";
	}

    public String getApplicationName() {
    	return "iTools";
    }

	protected void doActionToggleCenter() {
		Controller.getInstance(isSandBox).toggleCenterCategorized();
	}

	public void setDataWithoutCenter(String string) {
		rootNodeWithoutCenter = HtXMLReader.buildTree(new StringReader(string));
		setData(rootNodeWithoutCenter);
	}

	public void setDataWithCenter(String string) {
		rootNodeWithCenter = HtXMLReader.buildTree(new StringReader(string));
		setData(rootNodeWithCenter);
	}
	
	protected final class HtViewerMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			HtNode node = getNodeUnderMouse(e);
			if (node == null) {
				return;
			}
			String path = node2RootPath(node);
			if (path == null) {
				return;
			}

			setFiringChangeEvent(true);
			Controller.getInstance(isSandBox).firePropertyChange(
					new PropertyChangeEvent(this, Controller.SELECTED_PATH,
							null, path.split(DELIMITER)));
			setFiringChangeEvent(false);

			if (e.getClickCount() >= 2) {
				if (node != null) {
					MainFrame.getContent().openUrl(node.getUrl());
				}
			}
		}
	}


}