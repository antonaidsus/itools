package edu.ucla.loni.ccb.itools.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.dao.Security;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.model.RUser;

public class AddResourceDialog extends MyJDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	ResourceSpreadSheet spreadSheet = new ResourceSpreadSheet(true);
//    JButton addOther = new JButton("Add Others");
    private ResourceIndexTree tree = null;
    private String centerName;

    //ButtonPane buttonPane = new ButtonPane(buttonLabels);
    
    public AddResourceDialog(){
    	super(MainFrame.getContent().getWindowAncestor());
    	setUpGui();
    	//buttonPane.addActionListener(this);
//    	addOther.addActionListener(this);
    }
	public void setTargetTree(ResourceIndexTree tree) {
		this.tree = tree;
		
		DefaultMutableTreeNode node = tree.getSelectedNode();
		if (node != null) {			
			TreeNode[] path = node.getPath();
			int startI = 2;
			if (Descriptor.EXTERNAL_CENTER.equals(tree.getName())) {
				centerName = path[1].toString();
				startI = 3;
			} else {
				centerName = tree.getName();
			}
			
			String ontology = path[startI -1].toString();
			for (int i = startI; i < path.length; i++) {
				ontology += "-->" + path[i];
			}
			spreadSheet.setValue(Descriptor.PROP_ONTOLOGY, ontology);
		}
	}
	
	private void setUpGui() {
		getContentPane().add(spreadSheet, BorderLayout.CENTER);
		JPanel pane = new JPanel(new BorderLayout());
//		pane.add(addOther,BorderLayout.NORTH);
		//pane.add(buttonPane,BorderLayout.SOUTH);
		getContentPane().add(pane, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
//		Object o = e.getSource();
//		if (o == addOther) {
//			JOptionPane.showMessageDialog(this, "will show up dialog requstion a name and a value");
//			return;
//		}
		spreadSheet.displayResource(new NcbcResource());
		dispose();		
	}

	public void ok() {
		NcbcResource resource = spreadSheet.getResource();
		resource.setCenter(centerName);
		if (tree.displayer.isSandBox || !Security.currentRoleGood(RUser.EXPERTUSER)) {
			resource.setInSandBox(true); //the user could add to standard resource
		}	
		
		MainFrame.getContent().addResource(resource);		
		super.ok();
		GuiUtil.showMsg("The resource was added to the sandbox viewer");
	}
}
