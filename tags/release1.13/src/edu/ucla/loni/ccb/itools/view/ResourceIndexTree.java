package edu.ucla.loni.ccb.itools.view;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.gui.htviewer.Controller;
import edu.ucla.loni.ccb.itools.DataBank;
import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.Messages;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.dao.Security;
import edu.ucla.loni.ccb.itools.dao.remote.ProxyServer;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.model.RUser;
import edu.ucla.loni.ccb.itools.parser.NcbcResourceParser;
import edu.ucla.loni.ccb.itools.parser.Node;

/**
 * This class is main part of the left pane. It represents all resources of one
 * Center as JTree structure. Please see NcbcResourceIndexTree.java
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class ResourceIndexTree extends JTree implements IResourceContainer {
	private static final long serialVersionUID = 1L;
	public static final Logger logger = Logger.getLogger(ResourceIndexTree.class);
	public static final String RECOVER_FROM_LAST_UPDATE = "Recover from last update";
	/** PopUp menu triggered by mouse click. */
	protected static IndexTreePopup popup = new IndexTreePopup();

	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	// branch for nonstandard onTology nodes
	private DefaultMutableTreeNode othersNode;
	protected List<NcbcResource> allResources = new LinkedList<NcbcResource>();
	private boolean modified = false;
	private boolean initialized = false;
	private List<String> urlStrings = new ArrayList<String>();
	private Map<String, ArrayList<DefaultMutableTreeNode>> ontologyNodeMap = new HashMap<String, ArrayList<DefaultMutableTreeNode>>();
	private UpdateDialog dialog;
	StandardResourceDisplayer displayer;
	NcbcCenter ncbcCenter;

	public ResourceIndexTree(StandardResourceDisplayer displayer, NcbcCenter center) {
		this.displayer = displayer;
		this.ncbcCenter = center;

		root = new DefaultMutableTreeNode(new UB(center.getName(), null));
		model = new DefaultTreeModel(root);
		model.setAsksAllowsChildren(true);
		setRootVisible(false);
		setName(center.getName());

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
		List<NcbcResource> resources = getResourcesFromDao();
		if (resources == null)
			return;
		addResources(resources);
		model.reload();
		setInitialized(true);
	}

	protected List<NcbcResource> getResourcesFromDao() {
		return DataBank.getResources(ncbcCenter.getName());
	}

	public void load() {
		loadOnTology();
		loadResources();
	}

	public void loadOnTology() {
		loadOnTology(root);
		setOthersNode();
	}

	private void setOthersNode() { // I believe this is fast than other method.
		Enumeration<?> children = root.children();
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) children
					.nextElement();
			if (node.getUserObject().toString().equals(Descriptor.NON_STANDARD)) {
				othersNode = node;
				break;
			}
		}
	}

	public void loadOnTology(DefaultMutableTreeNode node) {
		for (Node xmlNode : DataBank.getOntology()) {
			addNode2Tree(node, xmlNode);
		}
	}

	public void removeNode(DefaultMutableTreeNode node) {
		model.removeNodeFromParent(node);
		NcbcResource r = ((UB) node.getUserObject()).data;
		// LOGGER.debug("does contain this rsc=" + allResources.contains(r));
		allResources.remove(r);
		DataBank.removeResource(r);
		// update htviewer
		Controller.getInstance(displayer.isSandBox).firePropertyChange(
				new PropertyChangeEvent(displayer, Controller.DATA_OF + toString(),
						"", "nodeRemoved"));
		
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
				logger.info(ncbcr.getName() + " failed to added due to sanbox");
			}
			return; // should be added to another
		}

		if (!ncbcr.isValid()) {
			logger.debug("NcbcResoruce no name defined, ignored\n"
					+ NcbcResourceParser.getSaveString(ncbcr));
			return;
		}

		allResources.add(ncbcr);

		String[] tokens = ncbcr.getOntology();
		// the ontology as parent Node
		for (String ontology : tokens) {
			String parentName = ontology.trim();
			List<DefaultMutableTreeNode> tnodes = ontologyNodeMap
					.get(parentName);
			if (tnodes == null) {
				// the ontology Node was not loaded add it to nonStandard
				DefaultMutableTreeNode node = addNode2Tree(othersNode,
						parentName, false);
				addResourceNodeAsChildOf(node, ncbcr);
			} else {
				for (DefaultMutableTreeNode node : tnodes) {
					addResourceNodeAsChildOf(node, ncbcr);
				}
			}
		}
	}

	private void addResourceNodeAsChildOf(DefaultMutableTreeNode parent,
			NcbcResource rsc) {
		DefaultMutableTreeNode node = addNode2Tree(parent, rsc.getName(), true);
		node.setAllowsChildren(false);
		((UB) node.getUserObject()).data = rsc;
	}

	public String getUrlString() {
		String urlString = null;
		if (urlStrings.size() > 0) {
			urlString = urlStrings.get(0);
		}
		return urlString;
	}

	public void setUrlString(String str) {
		urlStrings.add(str);
	}

	public void setInitialized(boolean b) {
		initialized = b;
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

	public List<NcbcResource> getResources() {
		return allResources;
	}

	public void displayAllResources() {
		displayer.displayResources(getAllResourceAndNode());
	}

	public void openNode(DefaultMutableTreeNode node) {
		displayer.openNode(node, this);
	}

	public void highlightNode(TreeNode node) {
		if (node == null) {
			return;
		} else {
			if (node.getAllowsChildren()) {
				String status = getResourceNodeNumber(node)
						+ " instance(s) available for '" + node + "'";
				MainFrame.getContent().setStatus(status);
			}
		}
	}

	
	/**
	 * Gets the number of node representing a resource under this treeNode. 
	 * It will recursive search its children.
	 * 
	 * @param node
	 * @return
	 */
	public int getResourceNodeNumber(TreeNode node) {
		int rv = 0;
		for (int i = 0; i < node.getChildCount(); i++) {
			TreeNode child = node.getChildAt(i);
			if (child.getAllowsChildren()) {
				rv += getResourceNodeNumber(child);
			} else {
				rv++;
			}
		}
		return rv;
	}

	public final void addResources(Collection<NcbcResource> rscs) {
		for (NcbcResource rsc : rscs) {
			if (rsc.getCenter() == null) {
				rsc.setCenter(ncbcCenter.getName());
			}
			addResource(rsc);
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
		if (!urlStrings.contains(urlStr)) {
			urlStrings.add(urlStr);
		}
		
		try {
			String msg = "please waiting, updating: " + getName() + " from: " + urlStr;
			MainFrame.getContent().setStatus(msg);
			logger.debug(msg);
			msg = readFromUrl(urlStr);

			if (msg.startsWith("good")) {
			    DataBank.refreshCenter(getName());
			    doUpdate();
			}
			
			MainFrame.getContent().setStatus("Updating from internet result: " + msg);

		} catch (Exception e) {
			MainFrame.getContent().setStatus("update failed, " + e);
			logger.error("update failed", e);
		}
	}

	protected String askUrlString() {
		if (dialog == null) {
			dialog = new UpdateDialog(MainFrame.getContent().getWindowAncestor());
		}
		
		dialog.getOkButton().setEnabled(true);
		String urlStr = getUrlString();
		if (urlStr == null) {
			urlStr = "This center don't have update site yet";
			dialog.getOkButton().setEnabled(false);
		}
		if (DaoFactory.getDaoFactory().getResourceDao().canRecover(ncbcCenter.getName())) {
			if (!urlStrings.contains(RECOVER_FROM_LAST_UPDATE)) {
				urlStrings.add(RECOVER_FROM_LAST_UPDATE);
			}
		}
		ComboBoxModel aModel = new DefaultComboBoxModel(urlStrings.toArray());

		dialog.urlText.setModel(aModel);
		dialog.setVisible(true);
		return dialog.ask() ? dialog.urlText.getSelectedItem().toString()
				: null;
	}

	protected String readFromUrl(String urlStr) throws IOException,
			MalformedURLException {
		Properties props = new Properties();
		props.put("method", "updateFromUrl");
		props.put("urlStr", urlStr);
		props.put("center", getName());

		return ProxyServer.talk2Server("resource.htm", props);
	}

	public void reload() {
		ontologyNodeMap.clear();
		getResources().clear();
		if (root != null) {
			root.removeAllChildren();
		}
        setInitialized(false); //to improve perfomance
		load();
	}

	protected void doUpdate() {
		logger.debug("reloade the viewers()");
		reload();
		// update htviewer
		PropertyChangeEvent pce = new PropertyChangeEvent(displayer, Controller.DATA_OF + toString(),"", "reload");
		Controller.getInstance(displayer.isSandBox).firePropertyChange(pce);
		Controller.getInstance(!displayer.isSandBox).firePropertyChange(pce);
	}

	public String toString() {
		return ncbcCenter.toString();
	}

	protected DefaultMutableTreeNode addNode2Tree(DefaultMutableTreeNode node,
			Node xmlNode) {
		DefaultMutableTreeNode tmp = addNode2Tree(node, xmlNode.getName(), false);
		for (Node child : xmlNode.getChildren()) {
			addNode2Tree(tmp, child);
		}

		return tmp;
	}

	protected DefaultMutableTreeNode addNode2Tree(DefaultMutableTreeNode p,
			String c, boolean leafNode) {
		DefaultMutableTreeNode nn = new DefaultMutableTreeNode(new UB(c, null));

		int index = getIndex(p, c);
		if (initialized) {
			model.insertNodeInto(nn, p, index);
		} else {
			p.insert(nn, index);
		}
		if (!leafNode) {
			ArrayList<DefaultMutableTreeNode> listnodes = ontologyNodeMap.get(c);
			if (listnodes == null) {
				listnodes = new ArrayList<DefaultMutableTreeNode>(5);
				ontologyNodeMap.put(c, listnodes);
			}
			listnodes.add(nn);
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

	public static boolean shouldBeLast(String name) {
		return Descriptor.NON_STANDARD.equals(name)
				|| Descriptor.NOVALUE.equals(name);
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
		int offset = Controller.getInstance(displayer.isSandBox).isCenterCategorized() 
		    ? 0 : 1;
		TreeNode[] path3 = tnode.getPath();
		int length = path3.length - offset;
		String[] strs = new String[length];
		// strs[0] = getName(); //Center name;
		for (int i = 0; i < length; i++) {
			strs[i] = path3[i + offset].toString();
		}
		return strs;
	}

	/**
	 * Expand the tree node represented by the StringTokenizer, the tokenizer
	 * should contains ontology + resourceName.
	 * 
	 * @param tokenizer
	 */
	public void expandPath(String[] tokens, boolean openNode) {
		if (tokens == null || tokens.length == 0) {
			return;
		}
		int index = 0;
		if (tokens[0].trim().equals("iTools")) {
			index++;
		}
		DefaultMutableTreeNode node = findNode(getRoot(), tokens, index);
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

	public List<ResourceAndViewNode> getAllResourceAndNode() {
		return getAllResourceAndNode(root, true);
	}

	public List<ResourceAndViewNode> getAllResourceAndNode(
			DefaultMutableTreeNode node, boolean recursive) {
		LinkedList<ResourceAndViewNode> resourceList = new LinkedList<ResourceAndViewNode>();
		for (int i = 0, I = node.getChildCount(); i < I; i++) {
			DefaultMutableTreeNode childAt = (DefaultMutableTreeNode) node
					.getChildAt(i);
			UB ub = (UB) childAt.getUserObject();
			if (ub.data != null) {
				resourceList
						.add(new ResourceAndViewNode(ub.data, childAt, this));
			} else if (recursive) {
				resourceList.addAll(getAllResourceAndNode(childAt, recursive));
			}
		}
		return resourceList;
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
