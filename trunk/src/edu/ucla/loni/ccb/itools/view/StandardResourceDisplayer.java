package edu.ucla.loni.ccb.itools.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Controller;
import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.view.ResourceIndexTree.UB;

/**
 * This class to display the NcbcResource in the traditional (the original )
 * way, contrary to the HT viewer. The GUI has two part, the left side is the
 * tree index, and right side pane is the display pane.
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class StandardResourceDisplayer extends JPanel implements PropertyChangeListener {
	private static final Logger logger = Logger.getLogger(StandardResourceDisplayer.class);

	private static final long serialVersionUID = 1L;
	TreeContainer trees;
	ViewerContainer resourceViewer = new ViewerContainer();

	IResourceContainer[] resourceContainers;
	JComboBox indexTreeCombo; // items are indexTreeItems + iatrTree +
	// ncbcTree

	ResourceIndexTree currentSelectedIndexTree;
	boolean isSandBox = false;

	public StandardResourceDisplayer(boolean sandBox) {
		setLayout(new BorderLayout());
		Controller.getInstance(sandBox).addPropertyChangeListener(this);
		this.isSandBox = sandBox;
		JPanel leftPane = new JPanel(new BorderLayout());

		List resourceContainersList = new ArrayList();
		List externalResourceContainersList = new ArrayList();

		List<NcbcCenter> centers = DaoFactory.getDaoFactory().getCenterDao().getCenters();
		logger.debug("number of center=" + centers.size());
		IResourceContainer c = null;
		for (NcbcCenter element : centers) {
			if (element.getName().equals("NCBC")) {
//				if (isSandBox) {
//					continue;
//				}
				c = new NcbcResourceIndexTree(this);	
				resourceContainersList.add(c);
			} else {
				if (element.isExternal()) {
				    c = new ResourceIndexTree(this,  element.getName(), true );
				    externalResourceContainersList.add(c);
				} else {
					c = new ResourceIndexTree(this,  element.getName(), false );
					resourceContainersList.add(c);
				}
			}
			c.setUrlString(element.getUrl());
		}
		
		resourceContainersList.addAll(externalResourceContainersList);
		
		resourceContainers = (IResourceContainer[]) resourceContainersList
				.toArray(new IResourceContainer[0]);
		logger.debug("ResourceContainernumbers=" + resourceContainers.length);
		trees = new TreeContainer(resourceContainers);

		indexTreeCombo = new JComboBox(resourceContainers);
		indexTreeCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IResourceContainer o = (IResourceContainer) indexTreeCombo
						.getSelectedItem();
				trees.display(o);
				setCurrentIndexTree();
				Controller.getInstance(isSandBox).firePropertyChange(
						new PropertyChangeEvent(StandardResourceDisplayer.this, Controller.SELECTED_PATH, null, new String[] { o.getName() }));
			}
		});

		setCurrentIndexTree();

		JPanel topleft = new JPanel(new GridLayout(2, 1));
		topleft.setBorder(new EmptyBorder(10, 5, 15, 5));
		topleft.add(new JLabel(" Resources:"));
		topleft.add(indexTreeCombo);

		leftPane.add(topleft, BorderLayout.NORTH);
		leftPane.add(trees, BorderLayout.CENTER);
		JPanel rightPane = new JPanel(new BorderLayout());
		rightPane.add(resourceViewer, BorderLayout.CENTER);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPane, rightPane);
		split.setOneTouchExpandable(true);

		JPanel standardPane = new JPanel(new BorderLayout());
		standardPane.add(new SearchPanel(this), BorderLayout.NORTH);
		standardPane.add(split, BorderLayout.CENTER);
		split.setDividerLocation(180);

		add(standardPane, BorderLayout.CENTER);
	}

	public void init() {
		for (int i = 0, I = resourceContainers.length; i < I; i++) {
			resourceContainers[i].load();
		}
	}

	public IResourceContainer getSelectedResourceContainer() {
		return (IResourceContainer) indexTreeCombo.getSelectedItem();
	}

	/**
	 * Get resources from centers. If allcenter is true, it will return all
	 * resources loaded. if allcenter is false, it just return current selected
	 * center's resources. The reason keep this method here instead of at DAO
	 * class is that, for applet mode, it can search from local, don't have to
	 * go to server.
	 * 
	 * @param allcenter
	 * @return
	 */
	public Iterator getResources(boolean allcenter) {
		if (allcenter) {
			LinkedList list = new LinkedList();
			for (int i = 0; i < resourceContainers.length; i++) {
				list.addAll(resourceContainers[i].getResources());
			}
			return list.iterator();
		} else {
			IResourceContainer rc = (IResourceContainer) indexTreeCombo
					.getSelectedItem();
			return rc.getResources().iterator();
		}
	}

	public ViewerContainer getResourceViewer() {
		return resourceViewer;
	}

	public void addResource(NcbcResource rsc) {
		String center = rsc.getCenter();
		IResourceContainer c = getIResourceContainerByName(center);
		if (c == null) {
			logger
					.info("Current Center be used, because could not find the IndexTree using centerName="
							+ center);
			c = (IResourceContainer) indexTreeCombo.getSelectedItem();
		}
		c.addResource(rsc);
	}

	public void openNode(DefaultMutableTreeNode node, ResourceIndexTree tree) {
		if (node == null) {
			return;
		}
		if (node.getAllowsChildren()) {
			ArrayList<NcbcResource> resourceList = null;
			int I = node.getChildCount();
			resourceList = new ArrayList<NcbcResource>(I);
			for (int i = 0; i < I; i++) {
				DefaultMutableTreeNode childAt = (DefaultMutableTreeNode) node
						.getChildAt(i);
				UB ub = (UB) (childAt).getUserObject();
				if (ub.data != null) {
					resourceList.add(ub.data);
				}
			}
			resourceViewer.setData(resourceList);
		} else {
			NcbcResource rsc = ((UB) ((DefaultMutableTreeNode) node)
					.getUserObject()).data;
			resourceViewer.setData(new ResourceAndViewNode(rsc, node, tree));
		}
		Controller.getInstance(isSandBox).firePropertyChange(new PropertyChangeEvent(this,"SelectPath", null, tree
				.getFullPath((DefaultMutableTreeNode) node)));
	}

	/**
	 * Displays the list of resource at the viewer, the resources should have
	 * been all loaded. It should be resposible synchronized with the HTViewer.
	 * 
	 * @param resources
	 */
	public void displayResources(List resources) {
		resourceViewer.setData(resources);
		if (resources.size() == 0) {
			return;
		}
		// update htPanel
		List tokens = new ArrayList(resources.size());
		for (Iterator iter = resources.iterator(); iter.hasNext();) {
			NcbcResource rsc = (NcbcResource) iter.next();
			tokens.add(rsc.getFirstFullPath());
		}
		Controller.getInstance(isSandBox).firePropertyChange(new PropertyChangeEvent(this,Controller.SELECTED_PATH, null, tokens));
	}

	private void setCurrentIndexTree() {
		Object selectedItem = indexTreeCombo.getSelectedItem();
		if (selectedItem instanceof IatrPane) {
			currentSelectedIndexTree = ((IatrPane) selectedItem).iatrTree;
		} else {
			currentSelectedIndexTree = (ResourceIndexTree) selectedItem;
		}
	}

	public void display(IResourceContainer container) {
		trees.display(container);
	}

	/**
	 * Expends the TreeNode whose path represented by the StringTokenizer,
	 * starting from center, ontology, and the name.
	 * 
	 * @param tokenizer
	 */
	public void expandTreeIndexNode(String[] tokens) {
		boolean centerCategorized = Controller.getInstance(isSandBox).isResourceCenterCategorized();
		logger.debug("Centerlized=" + centerCategorized + ", " + ArrayUtils.toString(tokens));
		if (centerCategorized) {
			String centername = tokens[0].trim();
			if (!centername.equals(getSelectedResourceContainer().getName())) {
				// selected center
				IResourceContainer item = getIResourceContainerByName(centername);
				if (item != null) {
					indexTreeCombo.setSelectedItem(item);
				}
			}
			String[] path = new String[tokens.length - 1];
			System.arraycopy(tokens, 1, path, 0, path.length);
			currentSelectedIndexTree.expandPath(path);
		} else {
			indexTreeCombo.setSelectedItem(getIResourceContainerByName("NCBC"));
			currentSelectedIndexTree.expandPath(tokens);
		}

	}

	public IResourceContainer getIResourceContainerByName(String name) {
		IResourceContainer itemAt = null;
		for (int i = 0; i < indexTreeCombo.getItemCount(); i++) {
			itemAt = (IResourceContainer) indexTreeCombo.getItemAt(i);
			if (name.equals(itemAt.getName())) {
				indexTreeCombo.setSelectedIndex(i);
				break;
			}
		}
		return itemAt;
	}

	class TreeContainer extends JPanel {
		/**
		 * Logger for this class
		 */
		private final Logger logger = Logger.getLogger(TreeContainer.class);

		CardLayout layout = new CardLayout();

		TreeContainer(IResourceContainer[] cs) {
			setLayout(layout);
			for (int i = 0; i < cs.length; i++) {
				JComponent c = (JComponent) cs[i];

				JPanel pane = new JPanel(new BorderLayout());
				pane.add(new JScrollPane(c), BorderLayout.CENTER);

				add(pane, c.getName());
			}

		}

		void display(IResourceContainer container) {
			layout.show(this, container.toString());
			MainFrame.getContent()
					.setStatus(
							container.getResources().size()
									+ " instance(s) available.");
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		logger.info("PropertyEvent received: source=" +evt.getSource() + ", property="+ evt.getPropertyName());

		if (evt.getSource() == this) {
			return;
		}
		
		if (evt.getPropertyName().equals(Controller.SELECTED_PATH)) {
			expandTreeIndexNode((String[]) evt.getNewValue());
		}
		
	}
	
	public String toString() {
		return "StandResourceDisplayer " + isSandBox;
	}
}
