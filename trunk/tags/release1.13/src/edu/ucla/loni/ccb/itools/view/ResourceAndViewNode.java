package edu.ucla.loni.ccb.itools.view;

import javax.swing.tree.DefaultMutableTreeNode;

import edu.ucla.loni.ccb.itools.model.NcbcResource;

public class ResourceAndViewNode {
	private NcbcResource rsc;
	private DefaultMutableTreeNode treeNode;
	private ResourceIndexTree tree;

	public ResourceAndViewNode(NcbcResource rsc,
			DefaultMutableTreeNode treeNode, ResourceIndexTree tree) {
		this.rsc = rsc;
		this.treeNode = treeNode;
		this.tree = tree;
	}

	public NcbcResource getRsc() {
		return rsc;
	}

	public DefaultMutableTreeNode getTreeNode() {
		return treeNode;
	}

	public ResourceIndexTree getTree() {
		return tree;
	}

	public void removeNodeFromTree() {
		tree.removeNode(treeNode);
	}
	
	public String[] getFullPath() {
		return tree.getFullPath(treeNode);
	}

}
