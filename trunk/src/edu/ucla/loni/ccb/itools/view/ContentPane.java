package edu.ucla.loni.ccb.itools.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.bouthier.hypertreeSwing.applet.HTPanel;
import net.bouthier.hypertreewhell.applet.HtwPanel;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Controller;
import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * This class is the Main GUI
 * 
 * @author Qunfei Ma
 * 
 */
public class ContentPane extends JApplet {
	private static final long serialVersionUID = 1L;
	public static final Logger LOGGER = Logger.getLogger(ContentPane.class);
	JTabbedPane regularViewerTabs;
	JTabbedPane sandboxViewerTabs;
	
	public static final String STAND_VIEWER = "Standard Viewer";
	public static final String HT_VIEWER = "HT Viewer";
	public static final String HTW_VIEWER = "HTW Viewer";
	
	public static final String REGULAR_RESOURCE = "Regular Resources";
	public static final String SANDBOX_RESOURCE = "Resources at SandBox";

	StandardResourceDisplayer regularViewer;
	StandardResourceDisplayer sandboxViewer;
	
	private HTPanel regularHtViewer;
	private HTPanel sandboxHtViewer;
	
	private HtwPanel regularHtwViewer;
	private HtwPanel sandboxHtwViewer;

	private Component selectedViewer;

	JPanel mStatusPanel;
	/** Shows simple information at the bottom of the frame. */
	protected JLabel mStatusLabel;

	Window windowAncestor;

	public Window getWindowAncestor() {
		if (windowAncestor == null) {
			windowAncestor = SwingUtilities.getWindowAncestor(this);
		}
		return windowAncestor;
	}

	public void init() {
		try {
			GuiUtil.modifyUIValues();
			MainFrame.setContent(this);
			if (Main.isAppletMode()) {
				Main.setServerUrl(this.getParameter("server.url"));
				Main.setSecureServerUrl(this.getParameter("secure_server.url"));
			}

			Main.loadProperties(false);
			LOGGER.debug("appletmode=" + Main.isAppletMode() + ", server.url="
					+ Main.getServerUrl());

			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					initializeGUI();
				}
			});

			// htviewer already loaded the resources.
			regularHtViewer.init();
			sandboxHtViewer.init();
			
			regularHtwViewer.init();
			sandboxHtwViewer.init();

			regularViewer.init();
			sandboxViewer.init();
		} catch (Exception e) {
			Main.LOGGER.warn("ContentPane init error:", e);
		}
	}

	public void start() {
		Main.setServerUrl(this.getParameter("server.url"));
		Main.setSecureServerUrl(this.getParameter("secure_server.url"));

		LOGGER.info(" ContentPane.start():" + Main.getServerUrl());
	}

	private void addHtViewer() {
		regularHtViewer = new HTPanel();
		regularHtViewer.setName(HT_VIEWER);

		regularViewerTabs.addTab(HT_VIEWER, regularHtViewer);
		regularViewerTabs.setComponentAt(0, regularHtViewer);
		
		sandboxHtViewer = new HTPanel(true);
		sandboxHtViewer.setName(HT_VIEWER);

		sandboxViewerTabs.addTab(HT_VIEWER, sandboxHtViewer);
		sandboxViewerTabs.setComponentAt(0, sandboxHtViewer);

	}
	
	private void addHtwViewer() {
		regularHtwViewer = new HtwPanel(false);
		regularHtwViewer.setName(HTW_VIEWER);
		regularViewerTabs.addTab(HTW_VIEWER, regularHtwViewer);
		
		sandboxHtwViewer = new HtwPanel(true);
		sandboxHtwViewer.setName(HTW_VIEWER);
		sandboxViewerTabs.addTab(HTW_VIEWER, sandboxHtwViewer);
	}


	private void initializeGUI() {
		setJMenuBar(new MenuBar());

		regularViewer = new StandardResourceDisplayer(false);
		sandboxViewer = new StandardResourceDisplayer(true);

		regularViewerTabs = new JTabbedPane();
		sandboxViewerTabs = new JTabbedPane();

		addHtViewer();
		addHtwViewer();
		
		regularViewerTabs.addTab(STAND_VIEWER, regularViewer);
		regularViewer.setName(STAND_VIEWER);

		sandboxViewerTabs.addTab(STAND_VIEWER, sandboxViewer);
		sandboxViewer.setName(STAND_VIEWER);

		regularViewerTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				selectedViewer = regularViewerTabs.getSelectedComponent();
				setStatus("Regular: " + regularViewerTabs.getSelectedComponent().getName());
			}
		});
		sandboxViewerTabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				selectedViewer = sandboxViewerTabs.getSelectedComponent();
				setStatus("Sandbox: " + sandboxViewerTabs.getSelectedComponent().getName());
			}
		});

      
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(REGULAR_RESOURCE, regularViewerTabs);
		tabbedPane.addTab(SANDBOX_RESOURCE, sandboxViewerTabs);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
	
		
//		getContentPane().add(regularViewerTabs, BorderLayout.CENTER);
		
		mStatusPanel = new JPanel(new BorderLayout());
		mStatusLabel = new JLabel();
		getContentPane().add(mStatusPanel, BorderLayout.SOUTH);
		mStatusPanel.add(BorderLayout.WEST, mStatusLabel);
		mStatusPanel.setPreferredSize(new Dimension(200, 25));
	}


	/**
	 * Sets the concise information of the status as <code>text</code>.
	 * 
	 * @param text
	 *            A String which is new status
	 */
	public void setStatus(String text) {
		_setStatus(text, Color.black);
	}

	/**
	 * Does the real work of setting the text of status bar.
	 * 
	 * @param text
	 *            a String represent current status
	 * @param c
	 *            a Color to show the importance of the status
	 */
	private void _setStatus(final String text, final Color c) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mStatusLabel.setForeground(c);
				mStatusLabel.setText(text);
			}
		});
	}

	public StandardResourceDisplayer getSandBoxDisplayer() {
		return sandboxViewer;
	}

	public StandardResourceDisplayer getStandardResourceDisplayer() {
		return regularViewer;
	}

	public void addResource(NcbcResource rsc) {
		if (rsc.isInSandBox()) {
			sandboxViewer.addResource(rsc);
		} else {
			regularViewer.addResource(rsc);
		}
		DaoFactory.getDaoFactory().getResourceDao().addResource(rsc);
	}

	/**
	 * Expends the TreeNode containing this NcbcResource and also updates the
	 * HTViewer.
	 * 
	 * @param rsc
	 */
	public void expandNode(NcbcResource rsc) {
		Controller.getInstance(rsc.isInSandBox()).firePropertyChange(
				new PropertyChangeEvent("Table", Controller.SELECTED_PATH, null, rsc.getFirstFullPath()));

		String url = rsc.getProperty(Descriptor.PROP_URL);
		if (url != null) {
			String[] strings = url.trim().split(" ");
			openUrl(strings[0]);
		}

	}

	public Component getSelectedViewer() {
		return selectedViewer;
	}

	public void openUrl(String url) {
		try {
			if (url == null || url.trim().length() == 0) {
				LOGGER.info("No URL availabe for this resource");
				return;
			}
			if (url.indexOf(":") == -1) {
				url = "http:" + url;
			}
			LOGGER.debug("url =" + url);
			if (Main.isAppletMode()) {
				getAppletContext().showDocument(new URL(url), "iTools");
			} else {
				showUrlInJEditPane(url);
			}
		} catch (Exception ex) {
			LOGGER.debug("Malformed URL : " + ex.getMessage());
		}
	}

	JEditorPane editorPane;
	MyJDialog dialog;
	String info = "This is simple display of url<br>";

	private void showUrlInJEditPane(String url) {
		if (editorPane == null) {
			editorPane = new JEditorPane();
			dialog = new MyJDialog(new JFrame());
			dialog.getContentPane().add(new JScrollPane(editorPane));
			dialog.setDialogMode(MyJDialog.CLOSE_DIALOG);
			dialog.setSize(800, 750);
		}
		dialog.getBanner().setSubtitle(info + url);

		try {
			editorPane.setPage(url);
			dialog.setVisible(true);
		} catch (IOException e) {
			LOGGER.debug("showUrl: Malformed URL : " + e.getMessage());
		}
	}

}
