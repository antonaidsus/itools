package edu.ucla.loni.ccb.itools.view;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.bouthier.hypertreeSwing.applet.HTVieweDataHelper;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Controller;
import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.Messages;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.dao.Security;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.model.RUser;
import edu.ucla.loni.ccb.itools.parser.CSVParser;
import edu.ucla.loni.ccb.itools.parser.CcbSoftwareClassificationExampleParser;
import edu.ucla.loni.ccb.itools.parser.NcbcResourceParser;
import edu.ucla.loni.ccb.itools.parser.RdfParser;

/**
 * This class is main part of the left pane. It represents all resources of one
 * Center as JTree structure.
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class ResourceIndexTree extends JTree implements IResourceContainer {
	private static final String RECOVER_FROM_LAST_UPDATE = "Recover from last update";
	private static final long serialVersionUID = 1L;
	/** Logger for this class */
	public static final Logger LOGGER = Logger
			.getLogger(ResourceIndexTree.class);
	/** PopUp menu triggered by mouse click. */
	protected static IndexTreePopup popup = new IndexTreePopup();

	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	//branch for nonstandard onTology nodes
	private DefaultMutableTreeNode othersNode;
	protected List<NcbcResource> allResources = new LinkedList<NcbcResource>();
	private boolean modified = false;
	private boolean initialized = false;
	private String urlString;
	private Map<String, ArrayList<DefaultMutableTreeNode>> ontologyNodeMap = new HashMap<String, ArrayList<DefaultMutableTreeNode>>();
	private UpdateDialog dialog;
	StandardResourceDisplayer displayer;
	private boolean forExternal = false;
	
	public ResourceIndexTree(StandardResourceDisplayer displayer, String name) {
		this(displayer, name, false);
	}
	
	public ResourceIndexTree(StandardResourceDisplayer displayer, String name, boolean external ) {
		this.displayer = displayer;
		forExternal = external;
		root = new DefaultMutableTreeNode(new UB(name, null));
		model = new DefaultTreeModel(root);
		model.setAsksAllowsChildren(true);
		setRootVisible(false);
		setName(name);

		setModel(model);
		putClientProperty("JTree.lineStyle", "Angled");
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				doPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				doPopup(e);
			}

			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (getRowForLocation(e.getX(), e.getY()) >= 0) {
						openNode((DefaultMutableTreeNode) getLastSelectedPathComponent());
					} else {
						setSelectionPath(null);
					}
				} else {
					highlightNode((TreeNode) getLastSelectedPathComponent());
				}
			}
		});
	}

	public void setModified(boolean b) {
		modified = b;
	}

	public boolean isModified() {
		return modified;
	}

	/** Invoked when a mouse right click occurs on a module of this processList. */
	void doPopup(MouseEvent e) {
		if (!isEnabled() || !e.isPopupTrigger()) {
			return;
		}
		int row = getRowForLocation(e.getX(), e.getY());
		if (row >= 0) {
			setSelectionRow(row);
		}
		popup.setIndexTree(this);
		popup.show(this, e.getX(), e.getY());
	}

	public void loadResources() {
		List<NcbcResource> resources = DaoFactory.getDaoFactory().getResourceDao()
				.load(getName());
		if (resources == null)
			return;
		addResources(resources);
		model.reload();
		setInitialized(true);
	}

	public void load() {
		loadOnTology();
		loadResources();
	}

	public void loadOnTology() {
		loadOnTology(root);
		setOthersNode();
	}

	private void setOthersNode() {
		Enumeration<?> children = root.children();
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
			if (node.getUserObject().toString().equals(Descriptor.NON_STANDARD)) {
                othersNode = node;
				break;
			}
		}
	}

	public void loadOnTology(DefaultMutableTreeNode node) {
		for (Iterator<String[]> i = HTVieweDataHelper.getOntology(); i.hasNext();) {
			addNode2Tree(node, i.next(), 0);
		}
	}

	public void removeNode(DefaultMutableTreeNode node) {
		model.removeNodeFromParent(node);
		NcbcResource r = ((UB) node.getUserObject()).data;
		// LOGGER.debug("does contain this rsc=" + allResources.contains(r));
		allResources.remove(r);

		// Here is very interesting, if allResources use HashSet, remove will
		// fail and contain return false.
		// jeff.ma noted 4/12/08
		// LOGGER.debug("the resource was removed from cache:" + remove);

		// LOGGER.debug(r.getName() + ",center=" + r.getCenter());
		// for (Iterator i = allResources.iterator(); i.hasNext();) {
		// NcbcResource rsc = (NcbcResource) i.next();
		// LOGGER.debug(rsc.getName() + ",center=" + r.getCenter());
		// if (rsc.getName().equals(r.getName())) {
		// LOGGER.debug("rsc == r =" + (rsc == r));
		// System.err.println("equals=" + r.equals(rsc));
		// }
		// }
		MainFrame.getContent().setStatus(
				Messages.getString("status.instance", new Integer(
						getResources().size())));
	}

	/**
	 * Adds a node to the tree under the root node, the node created from the
	 * ncbcr. This will not add resource to the database;
	 * 
	 * @param ncbcr
	 *            the NcbcResource
	 */
	public void addResource(NcbcResource ncbcr) {
		if (displayer.isSandBox != ncbcr.isInSandBox()) {
			if (initialized) {
				LOGGER.info(ncbcr.getName() + " failed to added due to sanbox");
			}
			return; // should be added to another
		}
		
		String name = ncbcr.getProperty(Descriptor.PROP_NAME);
		if (name == null) {
			LOGGER.debug("NcbcResoruce no name defined, ignored");
			LOGGER.debug(NcbcResourceParser.getSaveString(ncbcr, ""));
			return;
		}

		allResources.add(ncbcr);

		String[][] tokens = ncbcr.getOntology();
		// the ontology as parent Node
		for (String[] strs: tokens) {
			String parentName = strs[strs.length -1].trim();
			List<DefaultMutableTreeNode> tnodes = ontologyNodeMap.get(parentName);
			if (tnodes == null) {
				//the ontology Node was not loaded add it to nonStandard
				DefaultMutableTreeNode node = addNode2Tree(othersNode, strs, 0);
				addResourceNodeAsChildOf(node, ncbcr);
			} else {
				for (DefaultMutableTreeNode node : tnodes) {
					addResourceNodeAsChildOf(node, ncbcr);
				}
			}
		}
	}

	/**
	 * Adds a node to the tree under the node, the node created from the ncbc.
	 * This will not add resource to the database;
	 * 
	 * @param ncbc
	 *            the NcbcResource
	 */
//	public void addResource(DefaultMutableTreeNode parent, NcbcResource ncbcr) {
//
//
//			if (tokens[i].length == 1) { // only has parent case. should be in most cases now.
//				String parentName = tokens[i][0].trim();
//				List<DefaultMutableTreeNode> tnodes = ontologyNodeMap.get(parentName);
//				
//				if (tnodes == null) {
//					addNode2Tree(othersNode, tokens[i], 0);
//				}
//
//				for (Iterator iter = tnodes.iterator(); iter.hasNext();) {
//					DefaultMutableTreeNode node = (DefaultMutableTreeNode) iter
//							.next();
//					addResourceNodeAsChildOf(node, ncbcr);
//				}
//			} else {
//				DefaultMutableTreeNode node = addNode2Tree(parent, tokens[i], 0);
//				addResourceNodeAsChildOf(node, ncbcr);
//			}
//		}
//	}

	private void addResourceNodeAsChildOf(DefaultMutableTreeNode parent,
			NcbcResource rsc) {
		DefaultMutableTreeNode node = addNode2Tree(parent, rsc.getName());
		node.setAllowsChildren(false);
		((UB) node.getUserObject()).data = rsc;
	}

	public String getUrlString() {
		return urlString;
	}

	public void setUrlString(String str) {
		urlString = str;
	}

	public void setInitialized(boolean b) {
		initialized = b;
		model.reload();
	}

	public void setRoot(DefaultMutableTreeNode root) {
		this.root = root;
		model.setRoot(root);
		model.reload();
	}

	public DefaultMutableTreeNode getRoot() {
		return root;
	}

	public DefaultMutableTreeNode getSelectedNode() {
		return (DefaultMutableTreeNode) getLastSelectedPathComponent();
	}

	public NcbcResource getSelectedResource() {
		DefaultMutableTreeNode node = getSelectedNode();
		if (node == null)
			return null;

		NcbcResource rsc = ((UB) node.getUserObject()).data;
		return rsc;
	}

	public Collection<NcbcResource> getResources() {
		return allResources;
	}

	public void displayAllResources() {
		displayer.displayResources(new ArrayList<NcbcResource>(allResources));
	}

	public void openNode(DefaultMutableTreeNode node) {
		displayer.openNode(node, this);
	}

	public void highlightNode(TreeNode node) {
		if (node == null) {
			return;
		} else {
			String status = node.getAllowsChildren() ? getResourceNumber(node)
					+ " instance(s) available." : "";
			MainFrame.getContent().setStatus(status);
		}
	}

	/**
	 * Gets the number of the resources under this treeNode. It will recursive
	 * search its children.
	 * 
	 * @param node
	 * @return
	 */
	public int getResourceNumber(TreeNode node) {
		int rv = 0;
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child.getAllowsChildren()) {
				rv += getResourceNumber(child);
			} else {
				rv++;
			}
		}
		return rv;
	}

	public final void addResources(Collection<NcbcResource> rscs) {
		for (NcbcResource next : rscs){
			if (next.getCenter() == null)
				next.setCenter(getName());
			addResource(next);
		}
		// add dictionary to myTextField.
		Dictionary.addValue(rscs);
	}

	public void updateFromInternet() {
		if (!Security.hasPermission(RUser.EXPERTUSER)) {
			GuiUtil.showMsg("You don't have permission do this");
			return;
		}
		String urlStr = askUrlString();
		if (urlStr == null) {
			return;
		}
		try {
			List<NcbcResource> resources = null;
			if (urlStr.startsWith(RECOVER_FROM_LAST_UPDATE)) {
				resources = DaoFactory.getDaoFactory().getResourceDao().recover(getName());
				MainFrame.getContent().setStatus(getName() + ", " + RECOVER_FROM_LAST_UPDATE);

			} else {
				MainFrame.getContent().setStatus("starting to connect: " + urlStr);
				resources = readFromUrl(urlStr);
				if (resources == null) {
					return;
				}
				if (resources.size() == 0) {
					LOGGER.error("Empty resources, update aborted");
					MainFrame.getContent().setStatus(
					"Empty resources, update aborted");
					return;
				}
			}
			urlString = urlStr;
			doUpdate(resources);
		} catch (Exception e) {
			MainFrame.getContent().setStatus("update failed, " + e);
			LOGGER.error("update failed", e);
		}
	}

	protected String askUrlString() {
		if (dialog == null) {
			dialog = new UpdateDialog(MainFrame.getContent()
					.getWindowAncestor());
		}
		dialog.getOkButton().setEnabled(true);
		String urlStr = urlString;
		if (urlString == null) {
			urlStr = "This center don't have update site yet";
			dialog.getOkButton().setEnabled(false);
		}
		String[] data = new String[] {urlStr};
		if (DaoFactory.getDaoFactory().getResourceDao().canRecover(getName())) {
			data = new String[] {urlStr, RECOVER_FROM_LAST_UPDATE};			
		}
		ComboBoxModel aModel = new DefaultComboBoxModel(data);
		
		dialog.urlText.setModel(aModel );
		dialog.setVisible(true);
		return dialog.ask() ? dialog.urlText.getSelectedItem().toString() : null;
	}

	protected List<NcbcResource> readFromUrl(String urlStr) throws IOException,
			MalformedURLException {
		List<NcbcResource> resources;
		if (urlStr.endsWith(".xml")) {
			resources = NcbcResourceParser.parse(new URL(urlStr).openStream());
		} else if (urlStr.endsWith(".csv")) {
			resources = CSVParser.parse(new URL(urlStr).openStream());
		}  else if (urlStr.endsWith(".rdf")) {
			RdfParser rdfParser = new RdfParser();
			rdfParser.setForSandBox(false);
			resources = rdfParser.parse(new URL(urlStr).openStream());
		} else {
			resources = CcbSoftwareClassificationExampleParser
					.parseAsList(urlStr);
			String error = CcbSoftwareClassificationExampleParser
					.getPossibleError();
			if (error.trim().length() != 0) {
				String s = "The following may have error:\n\n" + error
						+ "\n\nWill you continue to store it to the system?";
				int i = JOptionPane.showConfirmDialog(this, s);
				if (i != JOptionPane.YES_OPTION) {
					resources = null;
				}
			}
		}
		return resources;
	}

	protected void clear() {
		clear(getRoot());
	}

	protected void clear(DefaultMutableTreeNode node) {
		ontologyNodeMap.clear();
		if (node == root) {
			getResources().clear();
		} else {
			removeResourcesOfCenter(node.toString());
		}
		node.removeAllChildren();
		model.reload();
	}

	private void removeResourcesOfCenter(String selectedCenterName) {
		for (Iterator<NcbcResource> iter = getResources().iterator(); iter.hasNext();) {
			NcbcResource rsc =  iter.next();
			if (selectedCenterName.equals(rsc.getCenter())) {
				iter.remove();
			}
		}
	}

	protected void doUpdate(List<NcbcResource> resources) {
		clear();
		loadOnTology();
		addResources(resources);
		setRoot(getRoot());
		// save the result to disk;
		DaoFactory.getDaoFactory().getResourceDao().setResources(resources);
		if (urlString != null) {
			MainFrame.getContent().setStatus(
					"update finished from: " + urlString);
		}
		// update htviewer
		Controller.INST.firePropertyChange(new PropertyChangeEvent(this, Controller.DATA_OF + getName(),"",""));

	}

	public String toString() {
		return getName() + (forExternal ? " (External)"  : "");
	}

	protected DefaultMutableTreeNode addNode2Tree(DefaultMutableTreeNode node,
			String[] tokens, int index) {
		while (index < tokens.length) {
			String token = tokens[index++].trim();
			DefaultMutableTreeNode tmp = getChild(node, token);
			if (tmp == null) {
				tmp = addNode2Tree(node, token);
				ArrayList<DefaultMutableTreeNode> listnodes = ontologyNodeMap.get(token);
				if (listnodes == null) {
					listnodes = new ArrayList<DefaultMutableTreeNode>(5);
					ontologyNodeMap.put(token, listnodes);
				}
				listnodes.add(tmp);
			}
			return addNode2Tree(tmp, tokens, index);
		}
		return node;
	}

	protected DefaultMutableTreeNode addNode2Tree(DefaultMutableTreeNode p,
			String c) {
		DefaultMutableTreeNode nn = new DefaultMutableTreeNode(new UB(c, null));
		int index = getIndex(p, c);
		if (initialized) {
			model.insertNodeInto(nn, p, index);
		} else {
			p.insert(nn, index);
		}
		return nn;
	}

	/**
	 * Gets the index of the node whose parent is <code>node</code>.
	 * 
	 * @param node
	 *            the parent of the node
	 * @param name
	 *            the name of this node
	 * @return an integer specifying the index of the node shose parent is
	 *         <code>node</code>
	 */
	int getIndex(TreeNode node, String name) {
		int count = node.getChildCount();
		if (count == 0) {
			return 0;
		}

		if (shouldBeLast(name)) {
			return count;
		}
		
		for (int i = 0; i < count; i++) {
			String oName = ((DefaultMutableTreeNode) node.getChildAt(i))
					.getUserObject().toString();
			if (name.compareTo(oName) < 0 || shouldBeLast(oName)) {
				return i;
			}
		}
		return count;
	}
	
	boolean shouldBeLast(String name) {
		return Descriptor.NON_STANDARD.equals(name) || Descriptor.NOVALUE.equals(name);
	}

	DefaultMutableTreeNode getChild(TreeNode node, String name) {
		for (int i = 0, I = node.getChildCount(); i < I; i++) {
			DefaultMutableTreeNode childAt = (DefaultMutableTreeNode) node
					.getChildAt(i);
			String oName = childAt.getUserObject().toString();
			if (name.equals(oName)) {
				return childAt;
			}
		}
		return null;
	}

	private static TreePath getTreePath(DefaultMutableTreeNode tnode) {
		TreeNode[] path = tnode.getPath();
		TreeNode[] visiblePath = new TreeNode[path.length - 1];
		System.arraycopy(path, 1, visiblePath, 0, visiblePath.length);

		return new TreePath(path);
	}

	public String[] getFullPath(DefaultMutableTreeNode tnode) {
		int offset = Controller.getInstance(displayer.isSandBox).isResourceCenterCategorized() ? 0 : 1;
		TreeNode[] path3 = tnode.getPath();
		int length = path3.length - offset;
		String[] strs = new String[length];
		// strs[0] = getName(); //Center name;
		for (int i = 0; i < length; i++) {
			strs[i] = path3[i + offset].toString();
		}
		// System.err.println(strs[0]);
		return strs;
	}

	/**
	 * Expand the tree node represented by the StringTokenizer, the tokenizer
	 * should contains ontology + resourceName.
	 * 
	 * @param tokenizer
	 */
	public void expandPath(String[] tokens, boolean openNode) {
		DefaultMutableTreeNode node = findNode(getRoot(), tokens, 0);
		if (node != null) {
			if (openNode)
				openNode(node);
			// System.out.println("itools nod was found and be opened;");
			TreePath selectedPath = getTreePath(node);
			if (node.isLeaf()) {
				node = (DefaultMutableTreeNode) node.getParent();
			}

			TreePath expandpath = getTreePath(node);
			expandPath(expandpath);
			setSelectionPath(selectedPath);
			scrollPathToVisible(selectedPath);
		}
	}

	public void expandPath(String[] tokens) {
		expandPath(tokens, false);
	}

	public DefaultMutableTreeNode findNode(DefaultMutableTreeNode m,
			String[] tokens, int index) {
		if (index >= tokens.length)
			return null;

		String n = tokens[index++].trim();
		Enumeration<?> enumeration = m.children();
		while (enumeration.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration
					.nextElement();
			LOGGER.debug(node.toString() + ", token=" + n + ",equals="
					+ (node.toString().equals(n)));

			if (node.toString().equals(n)) {
				if (index < tokens.length) {
					return findNode(node, tokens, index);
				}
				return node;
			}
		}
		return null;
	}

	class UpdateDialog extends MyJDialog {
		private static final long serialVersionUID = 1L;
		JComboBox urlText = new JComboBox();

		public UpdateDialog(Component component) {
			super(component);
			setModal(true);
			setupGui();
			setSize(500, 300);
			getBanner()
					.setSubtitle(
							"You will update the center from the following url. "
									+ "Please be aware you will <b>Wipe out</b> the current data at sever.");
			getBanner().setIcon(MainFrame.ITOOLS_ICON);

		}

		private void setupGui() {
			JPanel contentPane = new JPanel();
			contentPane.add(new JLabel("Url: ", JLabel.LEFT));
			contentPane.add(urlText);
			urlText.setEditable(true);
			getContentPane().add(contentPane);

		}

		public void ok() {
			super.ok();
		}
	}

	/**
	 * the data of the Node of ProcessList, If node allows children the object
	 * is the directory full name, otherwise the object is the PMData instanced
	 * by the file
	 */
	public static class UB implements java.io.Serializable {
		private static final long serialVersionUID = 1L;

		public UB(String name, NcbcResource data) {
			this.data = data;
			this.name = name;
		}

		public String toString() {
			return name;
		}

		NcbcResource data;
		String name;
	}
}
