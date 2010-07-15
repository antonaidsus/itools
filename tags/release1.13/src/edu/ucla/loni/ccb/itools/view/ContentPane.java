package edu.ucla.loni.ccb.itools.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
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

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.DataBank;
import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * This class is the Main GUI
 * Currently, it has two tabs: regular resources and resources in SandBox.
 * Each tabs has same multiple viewers.
 * 
 * @author Qunfei Ma
 * 
 */
public class ContentPane extends JApplet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ContentPane.class);
	
	private ViewerTabbedPane regularViewerTabs;
	private ViewerTabbedPane sandboxViewerTabs;

	public static final String REGULAR_RESOURCE = "Regular Resources";
	public static final String SANDBOX_RESOURCE = "Resources at SandBox";

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
			    Main.loadProperties(false);
			}

			logger.debug("appletmode=" + Main.isAppletMode() + ", server.url="
					+ Main.getServerUrl());

			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					initializeGUI();
				}
			});

			DataBank.loadData();
			loadDataToViwers();
		} catch (Exception e) {
			Main.LOGGER.warn("ContentPane init error:", e);
		}
	}

	public void loadDataToViwers() {
		getRegularViewerTabs().init();
		getSandboxViewerTabs().init();
	}
	
	private void initializeGUI() {
		setJMenuBar(new MenuBar());
		setRegularViewerTabs(new ViewerTabbedPane(this, false));
		setSandboxViewerTabs(new ViewerTabbedPane(this, true));

		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(REGULAR_RESOURCE, getRegularViewerTabs());
		tabbedPane.addTab(SANDBOX_RESOURCE, getSandboxViewerTabs());
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent changeevent) {
				String str = tabbedPane.getSelectedComponent().getName() + " selected";
				setStatus( str );
			}});
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel statusPanel = new JPanel(new BorderLayout());
		mStatusLabel = new JLabel();
		getContentPane().add(statusPanel, BorderLayout.SOUTH);
		statusPanel.add(BorderLayout.WEST, mStatusLabel);
		statusPanel.setPreferredSize(new Dimension(200, 25));
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

	public void addResource(NcbcResource rsc) {
		if (rsc.isInSandBox()) {
			getSandboxViewerTabs().addResource(rsc);
		} else {
			getRegularViewerTabs().addResource(rsc);
		}
		DataBank.addResource(rsc);
	}

	public void openUrl(String url) {
		try {
			if (url == null || url.trim().length() == 0) {
				logger.info("No URL availabe for this resource");
				return;
			}
			if (url.indexOf(":") == -1) {
				url = "http://" + url;
			}
			logger.debug("url =" + url);
			if (Main.isAppletMode()) {
				getAppletContext().showDocument(new URL(url), "iTools");
			} else {
				showUrlInJEditPane(url);
			}
		} catch (Exception ex) {
			logger.debug("Malformed URL : " + ex.getMessage());
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
			logger.debug("showUrl: Malformed URL : " + e.getMessage());
		}
	}

	public void setRegularViewerTabs(ViewerTabbedPane regularViewerTabs) {
		this.regularViewerTabs = regularViewerTabs;
	}

	public ViewerTabbedPane getRegularViewerTabs() {
		return regularViewerTabs;
	}

	public void setSandboxViewerTabs(ViewerTabbedPane sandboxViewerTabs) {
		this.sandboxViewerTabs = sandboxViewerTabs;
	}

	public ViewerTabbedPane getSandboxViewerTabs() {
		return sandboxViewerTabs;
	}

}
