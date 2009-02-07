package edu.ucla.loni.ccb.itools.view.review;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.ucla.loni.ccb.itools.Messages;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.model.ResourceReview;
import edu.ucla.loni.ccb.itools.view.GuiUtil;

public class SummaryPanel extends JPanel implements ActionListener {
	ReviewViewerContainer reviewViewerContainer;
	
	//ResourceReviewContainer reviewContainer;
	ResourceReview[] EMPTY_COMMENTS = new ResourceReview[0];
	ResourceReview[] reviewArray = EMPTY_COMMENTS;
	int currentIndex = 0;
	String currentResourceName = "";
	
//	String[] orderStrs= {"Nearest First","Oldest First", "Highest Rating First", "Lowest Rating First"};
	JLabel title = new JLabel();
	
	JLabel averageReviewLabel = new JLabel("Average User Comment:");
	JLabel averageReviewText = new JLabel();
	
	JLabel numberReviewLabel = new JLabel("Number of Reviews:");
	JLabel numberReviewText = new JLabel("0");
	
	JButton writeButton = new JButton("Write an online review");
	JLabel left = new JLabel(" and share your thoughts with other users");
	
	JPanel searchPanel = new JPanel();
	JLabel show = new JLabel("Show:");
	JComboBox orderChoices = new JComboBox(ReviewComparator.getImplementedNames());
	
	JPanel navigatePanel = new JPanel();
	JButton previous = new JButton("previous");
	JLabel currentText = new JLabel(" 0 of 0");
	JButton next = new JButton("next");
	JButton goButton = new JButton("Go to ");
	JTextField numberField = new JTextField(5);
	//List comments = new ArrayList(0);
	
	SummaryPanel() {
		setLayout(new GridBagLayout());
		setBackground(Color.white);
		writeButton.addActionListener(this);
		setupGui();
	}

	private void setupGui() {
		int row = 0;
		int gap = 3;
		Font font = title.getFont();
		font.deriveFont(Font.BOLD, font.getSize() + 10);
		title.setFont(font);
		GuiUtil.addComponent(this, title, 0, row, 3, 1);
		
		GuiUtil.addComponent(this, Box.createVerticalStrut(gap), 0, ++row, 1, 1);
		
		GuiUtil.addComponent(this, Box.createHorizontalStrut(8), 0, ++row, 1, 1);
		GuiUtil.addComponent(this, averageReviewLabel, 1, row, 1, 1);
		GuiUtil.addComponent(this, averageReviewText, 2, row, 1, 1);
		
		GuiUtil.addComponent(this, numberReviewLabel, 1, ++row, 1, 1);
		GuiUtil.addComponent(this, numberReviewText, 2, row, 1, 1);
		
		GuiUtil.addComponent(this, writeButton, 1, ++row, 1, 1);
		GuiUtil.addComponent(this, left, 2, row, 1, 1);
//		writeButton.setBackground(Color.white);
		
		GuiUtil.addComponent(this, Box.createVerticalStrut(gap), 0, ++row, 1, 1);
		
		GuiUtil.addComponent(this, searchPanel, 1, ++row, 2, 1);
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		searchPanel.setBackground(Color.white);
		searchPanel.add(show);
		searchPanel.add(orderChoices);
//		orderChoices.setBackground(Color.white);
		orderChoices.addActionListener(this);
		
		GuiUtil.addComponent(this, Box.createVerticalStrut(gap), 0, ++row, 1, 1);
		
		GuiUtil.addComponent(this, navigatePanel, 1, ++row, 2, 1);
		navigatePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		navigatePanel.setBackground(Color.white);
		navigatePanel.add(previous);
		navigatePanel.add(currentText);
		currentText.setMinimumSize(new Dimension(30, 5));
		navigatePanel.add(next);
		navigatePanel.add(Box.createHorizontalStrut(15));
		navigatePanel.add(goButton);
		navigatePanel.add(numberField);

//		goButton.setBackground(Color.white);
		goButton.addActionListener(this);
		numberField.addActionListener(this);

//		previous.setBackground(Color.white);
		currentText.setBackground(Color.white);
//		next.setBackground(Color.white);
		previous.addActionListener(this);
		next.addActionListener(this);		
	}
	
	public void setResource(NcbcResource rsc) {
		if (rsc == null) return;
		setResourceName(rsc.getName());
	}
	
	public void setResourceName(String resourceName) {
		currentResourceName = resourceName;
		reviewArray = EMPTY_COMMENTS;
		currentIndex = 0;

		reviewArray = (ResourceReview[]) DaoFactory.getDaoFactory().getReviewDao().getResourceReviews(currentResourceName).toArray(EMPTY_COMMENTS);

		title.setText(Messages.getString("review.viewer.title", currentResourceName));
		updateGui();
	}
	
	public void refresh() {
		reviewArray = (ResourceReview[]) DaoFactory.getDaoFactory().getReviewDao().getResourceReviews(currentResourceName).toArray(EMPTY_COMMENTS);
		updateGui();	
		System.out.println("refreshed, currentResourceName has " + reviewArray.length);
	}

	
//	public ResourceReviewContainer  getResourceCommnetContainer() {
//		return reviewContainer;
//	}

	private void updateGui() {
		int n = reviewArray.length;
		numberReviewText.setText(String.valueOf(n));
		ResourceReview review = null;
		
		if (n == 0) {
			averageReviewText.setIcon(null);
			averageReviewText.setText("N/A");
		} else {
			averageReviewText.setIcon(CommentPane.getIcon(getAverageRate()));
			averageReviewText.setText("");
		    review = reviewArray[0];
		}
		updateButtons();
		reviewViewerContainer.setValue(review);
		currentText.setText(( (n==0) ? 0 : 1) + " of " + n);
	}
	
	private int getAverageRate() {
		int I = reviewArray.length;
		if (I == 0) {
			return 8; //default 4 star;
		}
		
		int total = 0;
		for (int i = 0; i < I; i++) {
			total += reviewArray[i].getRate();
		}
		return total / I;
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o == writeButton) {
			writeNewReview();
			return;
		} else if (o == goButton || o == numberField) {
			int i = -1;

			try {
			    i = Integer.parseInt(numberField.getText().trim());
			} catch(Exception ex) {}
			if (i < 0 || i > reviewArray.length) {
				return;
			}
			currentIndex = i -1;
		} else if (o == previous) {
			currentIndex--;
		} else if (o == next) {
			currentIndex++;
		} else if ( o == orderChoices) {
			if (reviewArray.length < 2 ) return;
			currentIndex = 0;
			String name = (String) orderChoices.getSelectedItem();
			Arrays.sort(reviewArray, ReviewComparator.getInstanceBtName(name));
		}
		
        updateButtons();
		reviewViewerContainer.setValue(reviewArray[currentIndex]);
		currentText.setText((currentIndex+1) + " of " + reviewArray.length);
	}
	
	private void updateButtons() {
		previous.setEnabled(currentIndex != 0);
		next.setEnabled(currentIndex != reviewArray.length -1 && reviewArray.length != 0);		
	}

	private void writeNewReview() {
		CreateReviewDialog.showDialog(this, currentResourceName);		
	}

	public void setReviewViewerContainer(ReviewViewerContainer rcontainer) {
		this.reviewViewerContainer = rcontainer;		
	}

}
