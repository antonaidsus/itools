/* www.SOCR.ucla.edu
 * HyperTree Applet
 */

package net.bouthier.hypertreewhell.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.bouthier.hypertreeSwing.applet.HTVieweDataHelper;
import net.bouthier.hypertreewhell.HTDrawNode;
import net.bouthier.hypertreewhell.HTModelNode;
import net.bouthier.hypertreewhell.HTNode;
import net.bouthier.hypertreewhell.HTView;
import net.bouthier.hypertreewhell.HyperTree;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import edu.ucla.loni.ccb.itools.Controller;
import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.view.GuiUtil;
import edu.ucla.loni.ccb.itools.view.MainFrame;

public class HtwPanel extends JPanel implements PropertyChangeListener, ActionListener {
	private static final Logger logger = Logger.getLogger(HtwPanel.class);

	public JButton homeButton, backButton, forwardButton, searchButton,
			infoButton, displayModelButton, toogleCenterButton;

	private static final String SHOW_CENTER = "Show Centers";
	private static final String NO_CENTER = "No Centers";

	private JTextField textField;

	protected JPanel bottonsPanel;

	protected Vector foundHTDrawNodes = new Vector();

	protected int foundHTDrawNodes_Ind = 0;

	protected int foundHTDrawNodes_Size = 0;

	protected boolean isSandBox;

	protected ImageIcon homeIcon;

	protected ImageIcon backIcon;

	protected ImageIcon dimmedBackIcon;

	protected ImageIcon forwardIcon;

	protected ImageIcon dimmedForwardIcon;

	protected ImageIcon searchIcon;

	protected ImageIcon infoIcon;

	protected ImageIcon displayModelIcon;

	private HTDrawNode searchNode;
	private HTView view;

	public JButton drawlinesButton;
	private ImageIcon drawlines;

	public HtwPanel(boolean forSandbox) {
		this.isSandBox = forSandbox;
		setLayout(new BorderLayout());
		createButtonsPanel();
		Controller.getInstance(isSandBox).addPropertyChangeListener(this);
	}


	public void init() {
		removeAll();

		String value = HTVieweDataHelper
				.getAllResources4InteractiveViewer(isSandBox);
		HTXMLNode root = MyHTXMLReader.buildTree(new StringReader(value));

		resetForSearch(root);

		HyperTree hypertree = new HyperTree(root);
		view = hypertree.getView();
		view.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				HTDrawNode drawNode = view.getDrawNode(e);
				if (drawNode == null) {
					logger.info("HTWPanel mouseClicked:draw node is null");
					return;
				}
				String path = node2RootPath(drawNode.getHTModelNode());
				if (path == null) {
					return;
				}
				Controller.getInstance(isSandBox).firePropertyChange(
						new PropertyChangeEvent(HtwPanel.this,
								Controller.SELECTED_PATH, null, path
										.split(Descriptor.DELIMITER)));

				if (e.getClickCount() >= 2) {
					HTXMLNode node = (HTXMLNode) drawNode.getHTModelNode()
							.getNode();

					if (node != null) {
						MainFrame.getContent().openUrl(node.getURL());
					}
				}

			}
		});

		view.add(bottonsPanel, BorderLayout.NORTH);
		add(view, BorderLayout.CENTER);
		setToggleCenterButtonLabel();
	}


	protected void resetForSearch(HTXMLNode root) {
		// Set current Node to Root
		foundHTDrawNodes_Size = 1;
		foundHTDrawNodes_Ind = 0;
		foundHTDrawNodes = new Vector();
		foundHTDrawNodes.addElement((HTNode) root);
	}


	static class MyHTXMLReader extends HTXMLReader {
		public static HTXMLNode buildTree(Reader reader) {
			HTXMLReader htxmlreader = new HTXMLReader();
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				// factory.setNamespaceAware(true);
				SAXParser parser = factory.newSAXParser();
				parser.parse(new InputSource(reader), htxmlreader);
			} catch (Exception exception) {
				System.out.println("There is a problem : "
						+ exception.getMessage());
				exception.printStackTrace();
			}
			return htxmlreader.root;
		}
	}


	protected void createButtonsPanel() {
		createIcons();

		homeButton = new JButton("", homeIcon);
		homeButton.setToolTipText("iTools Home");
		homeButton.setBackground(Color.white);
		homeButton.setVerticalTextPosition(AbstractButton.CENTER);
		homeButton.setHorizontalTextPosition(AbstractButton.RIGHT);
		homeButton.setMnemonic(KeyEvent.VK_H);
		homeButton.setActionCommand("home");
		homeButton.addActionListener(this);
		homeButton.setEnabled(true);

		backButton = new JButton("", backIcon);
		backButton.setBackground(Color.white);
		backButton.setToolTipText("Back");
		backButton.setDisabledIcon(dimmedBackIcon);
		backButton.setVerticalTextPosition(AbstractButton.CENTER);
		backButton.setHorizontalTextPosition(AbstractButton.RIGHT);
		backButton.setMnemonic(KeyEvent.VK_B);
		backButton.setActionCommand("back");
		backButton.addActionListener(this);
		backButton.setEnabled(true);

		forwardButton = new JButton("", forwardIcon);
		forwardButton.setBackground(Color.white);
		forwardButton.setToolTipText("Forward");
		forwardButton.setDisabledIcon(dimmedForwardIcon);
		forwardButton.setVerticalTextPosition(AbstractButton.CENTER);
		forwardButton.setHorizontalTextPosition(AbstractButton.LEFT);
		forwardButton.setMnemonic(KeyEvent.VK_F);
		forwardButton.setActionCommand("forward");
		forwardButton.addActionListener(this);
		forwardButton.setEnabled(true);

		searchButton = new JButton("", searchIcon);
		searchButton.setBackground(Color.white);
		searchButton.setToolTipText("Search");
		searchButton.setVerticalTextPosition(AbstractButton.CENTER);
		searchButton.setHorizontalTextPosition(AbstractButton.LEFT);
		searchButton.setMnemonic(KeyEvent.VK_S);
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);
		searchButton.setEnabled(true);

		infoButton = new JButton("", infoIcon);
		infoButton.setBackground(Color.white);
		infoButton.setToolTipText("iTools HT Viewer Info");
		infoButton.setVerticalTextPosition(AbstractButton.CENTER);
		infoButton.setHorizontalTextPosition(AbstractButton.LEFT);
		infoButton.setMnemonic(KeyEvent.VK_I);
		infoButton.setActionCommand("info");
		infoButton.addActionListener(this);
		infoButton.setEnabled(true);

		displayModelButton = new JButton("", displayModelIcon);
		displayModelButton.setBackground(Color.white);
		displayModelButton.setToolTipText("iTools HT Display Model");
		displayModelButton.setVerticalTextPosition(AbstractButton.CENTER);
		displayModelButton.setHorizontalTextPosition(AbstractButton.LEFT);
		displayModelButton.setMnemonic(KeyEvent.VK_D);
		displayModelButton.setActionCommand("displayModel");
		displayModelButton.addActionListener(this);
		displayModelButton.setEnabled(true);

		toogleCenterButton = new JButton(SHOW_CENTER);
		toogleCenterButton.setBackground(Color.white);
		toogleCenterButton
				.setToolTipText("toogle centers info show/off in the view");
		toogleCenterButton.setVerticalTextPosition(AbstractButton.CENTER);
		toogleCenterButton.setHorizontalTextPosition(AbstractButton.LEFT);
		toogleCenterButton.setActionCommand("toogleCenter");
		toogleCenterButton.addActionListener(this);
		toogleCenterButton.setEnabled(true);

		textField = new JTextField(15);
		textField.setActionCommand("textField");
		textField.setToolTipText("Enter a keyword for Searching");
		textField.addActionListener(this);
		
		drawlinesButton = new JButton("", drawlines);
		drawlinesButton.setBackground(Color.white);
		drawlinesButton.setToolTipText("Turn On/Off Lines");
		drawlinesButton.setVerticalTextPosition(AbstractButton.CENTER);
		drawlinesButton.setHorizontalTextPosition(AbstractButton.LEFT);
		drawlinesButton.setMnemonic(KeyEvent.VK_E);
		drawlinesButton.setActionCommand("drawline");
		drawlinesButton.addActionListener(this);
		drawlinesButton.setEnabled(true);

		bottonsPanel = new JPanel();
		bottonsPanel.setBackground(Color.white);
		bottonsPanel.add(homeButton);
		bottonsPanel.add(backButton);
		bottonsPanel.add(forwardButton);
		bottonsPanel.add(textField);
		bottonsPanel.add(searchButton);
		bottonsPanel.add(infoButton);
		bottonsPanel.add(displayModelButton);
		bottonsPanel.add(toogleCenterButton);
		bottonsPanel.add(drawlinesButton);
	}

	protected void createIcons() {
		homeIcon = GuiUtil.getIcon("/go-home.png");
		backIcon = GuiUtil.getIcon("/go-previous.png");
		dimmedBackIcon = GuiUtil.getIcon("/go-previous.png");
		forwardIcon = GuiUtil.getIcon("/go-next.png");
		dimmedForwardIcon = GuiUtil.getIcon("/go-next.png");
		searchIcon = GuiUtil.getIcon("/system-search.png");
		infoIcon = GuiUtil.getIcon("/help-browser.png");
		displayModelIcon = GuiUtil.getIcon("/system-shutdown.png");
		drawlines = GuiUtil.getIcon("/view-fullscreen.png");
	}
	// User clicked one of the action buttons on the top
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("drawline")) {
			view.isDrawLine = !view.isDrawLine;
			view.repaint();
		} else if (e.getActionCommand().equals("home")) {
			view.draw.translateToOrigin(view.draw.drawRoot);
		} else if (e.getActionCommand().equals("back")) {
			if (--foundHTDrawNodes_Ind >= 0
					&& foundHTDrawNodes_Ind < foundHTDrawNodes_Size) {
				selectNode((HTDrawNode) (foundHTDrawNodes
						.elementAt(foundHTDrawNodes_Ind)));
			} else
				view.draw.restore();
		} else if (e.getActionCommand().equals("forward")) {
			if (++foundHTDrawNodes_Ind >= 0
					&& foundHTDrawNodes_Ind < foundHTDrawNodes_Size) {
				selectNode((HTDrawNode) (foundHTDrawNodes
						.elementAt(foundHTDrawNodes_Ind)));
			} else
				view.draw.restore();
		} else if (e.getActionCommand().equals("textField")
				|| e.getActionCommand().equals("search")) {
			String text = textField.getText();
			if (text != null)
				findHT_Node(text);
		} else if (e.getActionCommand().equals("info")) {
			try {
				JOptionPane
						.showMessageDialog(
								this,
								"iTools HyperTree Resource Viewer\n\n"
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
		} else if (e.getActionCommand().equals("displayModel")) {
			// kleinMode = !view.draw.kleinMode;
			view.action.switchKleinMode();
			// System.out.println("Display-Model Action Request ");
		} else if (e.getSource() == toogleCenterButton) {
			setToggleCenterButtonLabel();
			Controller.getInstance(isSandBox).toggleResourceCenterCategorized();
		}
	}
	
	protected void setToggleCenterButtonLabel() {
		toogleCenterButton.setText(
				Controller.getInstance(isSandBox).isResourceCenterCategorized() ? NO_CENTER : SHOW_CENTER);
	}


	/**
	 * Invoked by SearchButton of TextField actions - used to find all nodes
	 * that have this keyword (nodeName) in their names.
	 * 
	 * @param nodeName
	 */
	public void findHT_Node(String nodeName) {
		foundHTDrawNodes = new Vector(); // Reset the vector from previous
		// search
		foundHTDrawNodes_Ind = 0;
		foundHTDrawNodes_Size = 0;
		Enumeration enumNode = ((HTDrawNode) (view.draw.drawRoot))
				.children();
		// Enumeration enum = root.children();
		// System.out.println("Search Action Request: "+text+"\tEnum.number=
		// "+enum);
		while (enumNode.hasMoreElements()) {
			// searchNode = (HTDrawNode) enum.nextElement();
			searchNode = (HTDrawNode) enumNode.nextElement();
			// if
			// (searchNode.getName().toLowerCase().indexOf(text.toLowerCase())>=0){
			// foundHTDrawNodes.addElement(searchNode);
			// }
			searchNode.populateSearchVector(nodeName, foundHTDrawNodes);
			// System.out.println("Node: "+searchNode.getName());
		}
		foundHTDrawNodes_Size = foundHTDrawNodes.size();
		String status = foundHTDrawNodes_Size + " items found.";
		if (foundHTDrawNodes_Size > 0) {
			HTDrawNode drawNode = (HTDrawNode) (foundHTDrawNodes
					.elementAt(foundHTDrawNodes_Ind));
			selectNode(drawNode);
			status += ", current node is "
					+ node2RootPath(drawNode.getHTModelNode());
		}
		MainFrame.getContent().setStatus(status);
	}

	/**
	 * Translates the Node to the origin, it also will update the iTools ITrees.
	 * 
	 * @param node
	 */
	public void selectNode(HTDrawNode node) {
		view.draw.translateToOrigin(node);
		String str = node2RootPath(node.getHTModelNode());
		Controller.getInstance(isSandBox).firePropertyChange(
				new PropertyChangeEvent(this, Controller.SELECTED_PATH, null,
						str.split(Descriptor.DELIMITER)));
		MainFrame.getContent().setStatus(str);
	}

	public void selectNode(String[] tokens) {
		HTDrawNode node = findDrawNode(tokens);
		if (node != null) {
//			node = (HTDrawNode) view.draw.drawRoot;
			logger.info("Node found: " + node.getName());
			view.draw.translateToOrigin(node);
		}
	}

	/**
	 * @param tokens
	 *            an List of array of String
	 */
	public void selectNodes(List tokens) {
		logger.debug("selectNodes(ArrayList)");
		foundHTDrawNodes = new Vector(tokens.size()); // Reset the vector from
		// previous search
		foundHTDrawNodes_Ind = 0;
		foundHTDrawNodes_Size = tokens.size();
		for (Iterator iter = tokens.iterator(); iter.hasNext();) {
			foundHTDrawNodes.add(findDrawNode((String[]) iter.next()));
		}

		if (foundHTDrawNodes.size() > 0) {
			view.draw.translateToOrigin(((HTDrawNode) (foundHTDrawNodes
					.elementAt(foundHTDrawNodes_Ind))));
		}
	}

	public String node2RootPath(HTModelNode node) {
		if (node.getParent() != null) {
			String pstr = node2RootPath(node.getParent());
			return (pstr == null ? "" : pstr + Descriptor.DELIMITER)
					+ node.getName();
		} else {
			return null;
		}
	}

	/**
	 * Given the path as a tokenizer find the drawnode.
	 * 
	 * @param tokens
	 */
	public HTDrawNode findDrawNode(String[] tokens) {
		return findDrawNode(
				(HTDrawNode) view.draw.drawRoot,
				tokens, 0);
	}

	public HTDrawNode findDrawNode(HTDrawNode nodeComposite,
			String[] tokens, int index) {
		Enumeration enumNode = nodeComposite.children();
		String name = tokens[index++].trim();
		while (enumNode.hasMoreElements()) {
			HTDrawNode node = (HTDrawNode) enumNode
					.nextElement();
			if (node.getName().trim().equals(name)) {
				if (index < tokens.length) {
					return findDrawNode(node, tokens, index);
				} else {
					return node;
				}
			}
		}

		return nodeComposite;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		String propertyName = arg0.getPropertyName();
		logger.info("PropertyEvent received: source=" + arg0.getSource()
				+ ", property=" + propertyName + ", nvalue="
				+ arg0.getNewValue());
		if (arg0.getSource() == this) {
			return;
		} else if (propertyName.startsWith(Controller.SELECTED_PATH)) {
			if (arg0.getNewValue() instanceof String[]) {
				String[] newValue = (String[]) arg0.getNewValue();
				logger.info("newValue=" + ArrayUtils.toString(newValue));
				selectNode(newValue);
			} else {
				logger.warn("Dont understand the new value");
			}
		} else if (propertyName.startsWith(Controller.DATA_OF)) {
			init();
		} else if (propertyName.startsWith(Controller.CENTER_CATEGORIZED)) {
			init();
		}

	}

}