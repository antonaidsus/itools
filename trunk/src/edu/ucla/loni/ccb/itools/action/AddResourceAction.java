package edu.ucla.loni.ccb.itools.action;

import javax.swing.AbstractAction;

import edu.ucla.loni.ccb.itools.dao.Security;
import edu.ucla.loni.ccb.itools.model.RUser;
import edu.ucla.loni.ccb.itools.view.AddResourceDialog;
import edu.ucla.loni.ccb.itools.view.ResourceIndexTree;

public class AddResourceAction extends MyAction {
	private static AddResourceAction INSTANCE = new AddResourceAction();
	private ResourceIndexTree tree;
	AddResourceDialog dialog;
	public static AddResourceAction getInstance() {
		return INSTANCE;
	}
	
	public AddResourceAction() {
		putValue(AbstractAction.NAME, "Add Resource...");
	}
	
	public void setTargetTree(ResourceIndexTree tree) {
		this.tree = tree;
	}
	
	public void doAction() {
		if (!Security.hasPermission(RUser.NORMALUSER)) {
			return; //not login
		}
		if (dialog == null) {
			dialog = new AddResourceDialog();
			dialog.pack();
		}
		dialog.setTargetTree(tree);
		dialog.setVisible(true);
	}
}
