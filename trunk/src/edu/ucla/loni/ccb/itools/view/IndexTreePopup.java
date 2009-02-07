package edu.ucla.loni.ccb.itools.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import edu.ucla.loni.ccb.itools.action.AddResourceAction;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.dao.Security;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.model.RUser;
import edu.ucla.loni.ccb.itools.parser.BioSiteMapParser;
import edu.ucla.loni.ccb.itools.parser.RdfParser;

public class IndexTreePopup extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1L;
	private ResourceIndexTree tree;
	private DefaultMutableTreeNode selectedNode;

	private JMenuItem removeItem;
	private JMenuItem addResource;
	private JMenuItem commentItem;
	private JMenuItem updateItem;
	private JMenuItem displayAllItem;
	private JMenuItem viewSource;
	private JMenuItem viewSourceRDF;
	private JMenuItem viewSourceBiositemap;

	public IndexTreePopup() {
		addResource = add(AddResourceAction.getInstance());
		addSeparator();
		commentItem = addMenuItem("Comment");
		removeItem = addMenuItem("Delete");
		addSeparator();

		updateItem = addMenuItem("Update From Center Web");
		addSeparator();
		displayAllItem = addMenuItem("Display All Resources");
		addSeparator();
		viewSource = addMenuItem("View the Selected Resource");

		JMenu dumpXmlItem = new JMenu("Export To XML");
		dumpXmlItem.add(viewSource);
		viewSourceBiositemap = new JMenuItem(
				"View All Resources In Biositemap Format");
		viewSourceBiositemap.addActionListener(this);

		viewSourceRDF = new JMenuItem("View All Resources In RDF Format");
		viewSourceRDF.addActionListener(this);
		dumpXmlItem.add(viewSourceBiositemap);
		dumpXmlItem.add(viewSourceRDF);
		add(dumpXmlItem);

	}

	void setIndexTree(ResourceIndexTree tree) {
		this.tree = tree;
		selectedNode = tree.getSelectedNode();
		boolean isLeaf = selectedNode != null
				&& !selectedNode.getAllowsChildren();
		boolean nosandbox = !tree.displayer.isSandBox;

		removeItem.setEnabled(isLeaf);
		viewSource.setEnabled(isLeaf);
		commentItem.setEnabled(isLeaf && nosandbox);
		addResource.setEnabled(!isLeaf);

		updateItem.setEnabled(nosandbox);

		AddResourceAction.getInstance().setTargetTree(tree);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == displayAllItem) {
			tree.displayAllResources();
		} else {
			NcbcResource selectedResource = tree.getSelectedResource();

			if (source == removeItem) {
				if (!Security.hasPermission(RUser.EXPERTUSER)) {
					GuiUtil
							.showMsg("You don't have permission to delete resource.");
					return;
				}
				// this won't work for the case of multiple onTologies. The
				// other one will kept until restart
				tree.removeNode(tree.getSelectedNode());
				DaoFactory.getDaoFactory().getResourceDao().removeResource(
						selectedResource);
			} else if (source == commentItem) {
				StandardResourceDisplayer displayer = MainFrame.getContent()
						.getStandardResourceDisplayer();
				displayer.getResourceViewer().display(
						ViewerContainer.COMMENT_TEXT);
				displayer.getResourceViewer().getCommentViewer().display(
						selectedResource);
			} else if (source == viewSource) {
				String str = (selectedResource.isInSandBox() ? "This resource currently is in SandBox.\n"
						: "")
						+ BioSiteMapParser.getSaveString(selectedResource);
				GuiUtil.showMsg(str);
			} else if (source == viewSourceRDF) {
				String str = "";
				try {
					RdfParser.getSaveString(tree.allResources);
				} catch (Exception e1) {
					str  = "Export to RDF format wrong, due to wrong data.\n\n" + e1;
				}
				GuiUtil.showMsg(str);
			} else if (source == viewSourceBiositemap) {
				String str = BioSiteMapParser.getSaveString(tree.allResources);
				GuiUtil.showMsg(str);
			} else if (source == updateItem) {
				tree.updateFromInternet();
			}
		}
	}

	private JMenuItem addMenuItem(String cmd) {
		JMenuItem item = this.add(cmd);
		item.addActionListener(this);
		return item;
	}

}
