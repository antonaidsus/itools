package edu.ucla.loni.ccb.itools.view.review;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.dao.Security;
import edu.ucla.loni.ccb.itools.model.RUser;
import edu.ucla.loni.ccb.itools.model.ResourceReview;
import edu.ucla.loni.ccb.itools.view.ButtonPane;
import edu.ucla.loni.ccb.itools.view.MainFrame;
import edu.ucla.loni.ccb.itools.view.MyJDialog;

/**
 * This class shows the dialog to let user write a review (comment) on the product.
 * It provides to Panels, creating Pane let use to type, and preview Pane to let user
 * to check the comment before submit.
 * 
 * @author Jeff Qunfei Ma
 *
 */
public class CreateReviewDialog extends MyJDialog {
	CardLayout layout = new CardLayout();
	JPanel contentPane = new JPanel(layout);

	CreateReviewPanel creatingPanel = new CreateReviewPanel();
	PreviewReviewPanel previewPanel;
	SummaryPanel summaryPanel;
	String resourceName = "";
	
	public static void showDialog(SummaryPanel panel, String resourceName) {
		if (!Security.hasPermission(RUser.NORMALUSER)) {
			return;
		}
		MyJDialog dialog = new CreateReviewDialog(panel, resourceName);
		dialog.setVisible(true);
	}
	
    public CreateReviewDialog(SummaryPanel panel,String resourceName) {
    	super(MainFrame.getContent().getWindowAncestor());
    	this.summaryPanel = panel;
    	this.resourceName = resourceName;
    	previewPanel = new PreviewReviewPanel(panel);
    	setupGui();
    	setTitle("Creating Review for " + resourceName);

    	pack();
    }

	private void setupGui() {
		setContentPane(contentPane);
		contentPane.add(creatingPanel, "edit");
		contentPane.add(previewPanel, "preview");
		creatingPanel.setDialog(this);
	}
	
	public void showPreview() {
		ResourceReview resourceComment = creatingPanel.getResourceComment();
		resourceComment.setResourceName(resourceName);
		previewPanel.setResourceComment(resourceComment);
		layout.show(contentPane,"preview");
	}	
    
	public class PreviewReviewPanel extends JPanel implements ActionListener {
        ReviewViewer reviewViewer;
        ButtonPane buttonP = new ButtonPane(new String[] {"Edit", "Save", "Cancel"});
        
        public PreviewReviewPanel(SummaryPanel panel) {
        	reviewViewer = new ReviewViewer(panel, true);
        	setLayout(new BorderLayout());
        	add(reviewViewer, BorderLayout.CENTER);
        	add(buttonP, BorderLayout.SOUTH);
        	buttonP.addActionListener(this);
        }
        
        public void setResourceComment(ResourceReview comment) {
        	reviewViewer.setValue(comment);
        }

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("Edit")) {
				layout.show(contentPane, "edit");
				creatingPanel.setResource(reviewViewer.getValue());
			} else if (cmd.equals("Save")) {
				 DaoFactory.getDaoFactory().getReviewDao().add(reviewViewer.getValue());
				 dispose();
				 summaryPanel.refresh();
			} else if (cmd.equals("Cancel")) {
				 dispose();
			}
		}
	}
}
