package edu.ucla.loni.ccb.itools.view.review;

import java.awt.CardLayout;

import javax.swing.JPanel;

import edu.ucla.loni.ccb.itools.model.ResourceReview;

public class ReviewViewerContainer extends JPanel {
	public static final String COMMENT_AVAILABLE ="comments_available";
	public static final String COMMENT_NOT_AVAILABLE ="comments_not_available";
	

    private CardLayout layout = new CardLayout();
    private ReviewViewer reviewViewer;

    ReviewViewerContainer(ReviewViewer reviewViewer) {
    	this.reviewViewer = reviewViewer;
	    setLayout(layout);
	    JPanel emptyPane = new JPanel();
		add(reviewViewer, COMMENT_AVAILABLE);
		add(emptyPane, COMMENT_NOT_AVAILABLE);
    }
    
	public void setValue(ResourceReview comment) {
		if (comment == null) {
			layout.show(this, COMMENT_NOT_AVAILABLE);
		} else {
			reviewViewer.setValue(comment);
			layout.show(this, COMMENT_AVAILABLE);
		}
	}
}

