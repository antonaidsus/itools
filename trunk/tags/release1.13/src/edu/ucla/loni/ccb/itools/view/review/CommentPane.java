package edu.ucla.loni.ccb.itools.view.review;

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JPanel;

import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.view.GuiUtil;

public class CommentPane extends JPanel {
	private static Icon[] starIcons = new Icon[10];
	private static String[] ids={"1-0","1-0","1-5","2-0","2-5","3-0","3-5","4-0","4-5","5-0"};
	static {   
		for (int i = 0; i < 10; i++) {
		    starIcons[i] = GuiUtil.getIcon("/stars-" + ids[i] + ".gif");
		}
	}
	
	SummaryPanel summaryPane = new SummaryPanel();
	ReviewViewerContainer reviewViewerContainer = new ReviewViewerContainer(new ReviewViewer(summaryPane));
	
	public CommentPane() {
		setLayout(new BorderLayout());
		add(summaryPane, BorderLayout.NORTH);
		
		add(reviewViewerContainer, BorderLayout.CENTER);	
		summaryPane.setReviewViewerContainer(reviewViewerContainer);
	}
	
	public void display(NcbcResource resource) {
		summaryPane.setResource(resource);	
	}
	
	public static Icon getIcon(int rate) {
		if (rate < 1) rate = 1;
		if (rate > 10)rate = 10;
		return starIcons[rate-1];
	}	
}
