package edu.ucla.loni.ccb.itools.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
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

import edu.ucla.loni.ccb.gui.htviewer.Controller;
import edu.ucla.loni.ccb.itools.DataBank;
import edu.ucla.loni.ccb.itools.Util;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.HtViewerDataHelper;
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
	ViewerContainer resourceViewer = new ViewerContainer(); //right side pane.
	TreeContainer trees = new TreeContainer();

	JComboBox indexTreeCombo; 
	ResourceIndexTree[] resourceContainers;
	ResourceIndexTree currentSelectedIndexTree;
	
	boolean isSandBox = false;
	private  boolean firingChangeEvent;
	
	public boolean isFiringChangeEvent() {
		return firingChangeEvent;
	}

	public void setFiringChangeEvent(boolean firingChangeEvent) {
		this.firingChangeEvent = firingChangeEvent;
	}

	public StandardResourceDisplayer(boolean sandBox) {
		setLayout(new BorderLayout());
		Controller.getInstance(sandBox).addPropertyChangeListener(this);
		this.isSandBox = sandBox;
		JPanel leftPane = new JPanel(new BorderLayout());

		indexTreeCombo = new JComboBox();
		indexTreeCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ResourceIndexTree o = (ResourceIndexTree) indexTreeCombo
						.getSelectedItem();
				trees.display(o);
				currentSelectedIndexTree = o;
				if (isFiringChangeEvent()) {
					//sometimes action change is due to synchronized;
					//and set true in the propertyChanged();
					return;
				}
				setFiringChangeEvent(true);
				Controller.getInstance(isSandBox).firePropertyChange(
						new PropertyChangeEvent(StandardResourceDisplayer.this, Controller.SELECTED_PATH, null, new String[] { o.toString() }));
				setFiringChangeEvent(false);
			}
		});

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

	private void sort(List<IResourceContainer> resourceContainersList) {		
		Collections.sort(resourceContainersList, new Comparator<IResourceContainer>() {
			public int compare(IResourceContainer arg0, IResourceContainer arg1) {
				String n0 = arg0.getName();
				String n1 = arg1.getName();
				if (n0.equals("NCBC")) {
				    return -1;
				} else if (n1.equals("NCBC")) {
					return 1;
				} else {
					return n0.compareTo(n1);
				}
				
			} });		
	}

	public void init() {
		List<IResourceContainer> resourceContainersList = new ArrayList<IResourceContainer>();

		IResourceContainer c = null;
		for (NcbcCenter center : DataBank.getCenters()) {
			if (center.getName().equals("NCBC")) {
				c = new NcbcResourceIndexTree(this, center);	
			} else {
				c = new ResourceIndexTree(this,  center);
			}
			
			c.setUrlString(center.getUrl());
			resourceContainersList.add(c);
		}
		
		sort(resourceContainersList);
		
		resourceContainers = (ResourceIndexTree[]) resourceContainersList
				.toArray(new ResourceIndexTree[0]);
		trees.setResourceContainers(resourceContainers);
		indexTreeCombo.setModel(new DefaultComboBoxModel(resourceContainers));
		setCurrentIndexTree();
		
		for (int i = 0, I = resourceContainers.length; i < I; i++) {
			resourceContainers[i].load();
			logger.info(resourceContainers[i].toString() + " loaded, resource= " + resourceContainers[i].getResources().size() 
					+ ", node=" + resourceContainers[i].getAllResourceAndNode().size());
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
	public Iterator<ResourceAndViewNode> getResources(boolean allcenter) {
		ResourceIndexTree tmp = null;
		if (allcenter) {
			LinkedList<ResourceAndViewNode> list = new LinkedList<ResourceAndViewNode>();
			for (int i = 0; i < resourceContainers.length; i++) {
				tmp = (ResourceIndexTree) resourceContainers[i];
				list.addAll(tmp.getAllResourceAndNode());
			}
			return list.iterator();
		} else {
			tmp = (ResourceIndexTree) indexTreeCombo.getSelectedItem();
			return tmp.getAllResourceAndNode().iterator();
		}
	}

	public ViewerContainer getResourceViewer() {
		return resourceViewer;
	}

	/**
	 * Add the NcbcResoruce to the Displayer, It don't need to update the database.
	 * @param rsc
	 */
	public void addResource(NcbcResource rsc) {
		String center = rsc.getCenter();
		IResourceContainer c = getIResourceContainerByName(center);
		if (c == null) {
			logger.info("Current Center be used, because could not find the IndexTree using centerName="
							+ center);
			c = (IResourceContainer) indexTreeCombo.getSelectedItem();
		}
		c.addResource(rsc);
		Controller.getInstance(isSandBox).firePropertyChange(new PropertyChangeEvent(this, Controller.DATA_OF + c.toString(),"","resourceAdded"));

	}

	public void openNode(DefaultMutableTreeNode node, ResourceIndexTree tree) {
		if (node == null) {
			return;
		}
		if (node.getAllowsChildren()) {
			List<ResourceAndViewNode> resourceList = tree.getAllResourceAndNode(node, false);
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
	 * been all loaded. 
	 * 
	 * @param resources
	 */
	public void displayResources(List<ResourceAndViewNode> resources) {
		resourceViewer.setData(resources);
		if (resources.size() == 0) {
			return;
		}
	}

	private void setCurrentIndexTree() {
		Object selectedItem = indexTreeCombo.getSelectedItem();
		currentSelectedIndexTree = (ResourceIndexTree) selectedItem;
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
		boolean centerCategorized = Controller.getInstance(isSandBox).isCenterCategorized();
		logger.debug("Centerlized=" + centerCategorized + ", " + ArrayUtils.toString(tokens));
		if (tokens[0].equals(HtViewerDataHelper.ITOOLS)) {
			String[] tmp = new String[tokens.length-1];
			System.arraycopy(tokens, 1, tmp, 0, tmp.length);
			tokens = tmp;
		}

		if (tokens.length == 0) {
			return;
		}
		
		if (centerCategorized) {
			String centername = tokens[0].trim();
			if (!centername.equals(currentSelectedIndexTree.ncbcCenter.getName())) {
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
		ResourceIndexTree itemAt = null;
		for (int i = 0; i < indexTreeCombo.getItemCount(); i++) {
			itemAt = (ResourceIndexTree) indexTreeCombo.getItemAt(i);
			if (name.equals(itemAt.ncbcCenter.getName())) {
				indexTreeCombo.setSelectedIndex(i);
				break;
			}
		}
		return itemAt;
	}

	class TreeContainer extends JPanel {
		CardLayout layout = new CardLayout();

		TreeContainer() {
			setLayout(layout);
		}
		
		void setResourceContainers(IResourceContainer[] cs) {
			for (int i = 0; i < cs.length; i++) {
				JComponent c = (JComponent) cs[i];

				JPanel pane = new JPanel(new BorderLayout());
				pane.add(new JScrollPane(c), BorderLayout.CENTER);

				add(pane, c.toString());
			}
		}

		void display(IResourceContainer container) {
			layout.show(this, container.toString());
			String str = container + " has " + container.getResources().size() + " resources";
			if (container.getResources().size() > 0 && container instanceof ResourceIndexTree) {
				ResourceIndexTree rTree = (ResourceIndexTree) container;
				str += " and stored in " + rTree.getAllResourceAndNode().size() + " nodes.";		
			} else {
				str += " check in " + (isSandBox ? "Regular " : "SandBox ");
			}
			MainFrame.getContent().setStatus( str );
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == this || isFiringChangeEvent()) {
			return;
		}
		
		String propertyName = evt.getPropertyName();
		logger.info("PropertyEvent received: source=" +evt.getSource() + ", property="+ propertyName + "value=" + Util.toString(evt.getNewValue()));
		setFiringChangeEvent(true); //so if changing the comboBox, no changeEvnet firing.
		if (propertyName.equals(Controller.SELECTED_PATH)) {
			expandTreeIndexNode((String[]) evt.getNewValue());
		} else if (propertyName.startsWith(Controller.DATA_OF)) {
			//the resource was deleted, need reload, cause it may have 1+ ontology.
			//or a new resource is added
			//or the center was updated.
			String centerName = propertyName.substring(Controller.DATA_OF.length());
			IResourceContainer c = getIResourceContainerByName(centerName);
			if (c == null) {
				logger.info("could not find the IndexTree using centerName=" + centerName);
			} else {
			    c.reload();
			}
		}
		setFiringChangeEvent(false);
	}
	
	public String toString() {
		return "StandResourceDisplayer " + isSandBox;
	}
}
