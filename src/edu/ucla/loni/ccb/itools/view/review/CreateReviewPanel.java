package edu.ucla.loni.ccb.itools.view.review;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.l2fprod.common.swing.BannerPanel;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.model.ResourceReview;
import edu.ucla.loni.ccb.itools.view.GuiUtil;
import edu.ucla.loni.ccb.itools.view.MainFrame;

public class CreateReviewPanel extends JPanel implements ActionListener {
	private static CreateReviewDialog dialog;
	
	BannerPanel banner = new BannerPanel();
	
	String subtitle = "Tell us what you think. Write a review of this item and share your opinions with others.<br>" +
			" Please be sure to focus your comments on the product<br> ";
	JPanel content = new JPanel();
	JLabel label0 = new JLabel("On a scale of 1 to 5 stars, with 5 stars being the best, ");
	JLabel label1 = new JLabel("How do you rate this item? ");
	String[] rates = {"-", "1 star","2 stars","3 stars","4 stars","5 stars"};
	JComboBox itemRate = new JComboBox(rates);
	JLabel label2 = new JLabel("Please enter a title for your comment: ");
	JTextField titleText = new JTextField(30);
	JLabel label3 = new JLabel("Type your comment in the space below:");
	CommentTextArea reviewTextArea = new CommentTextArea();
	
	JButton previewB = new JButton("Preview your Comment");
	
	public CreateReviewPanel() {
		setupGui();
	}
	
	private void setupGui() {
		setLayout(new BorderLayout());
		add(banner, BorderLayout.NORTH);
		banner.setSubtitle(subtitle);
		banner.setIcon(MainFrame.ITOOLS_ICON);
		add(content, BorderLayout.CENTER);
		try {
			setContentPane();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//add(previewB, BorderLayout.SOUTH);
		previewB.addActionListener(this);
	}

	private void setContentPane() throws AWTException {
		content.setLayout(new GridBagLayout());
		JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pane.add(label1);
		pane.add(itemRate);
		int row = 0;
		GuiUtil.addComponent(content, label0, 0, row++, 1, 1);
		GuiUtil.addComponent(content, pane, 0, row++, 1, 1);
		GuiUtil.addComponent(content, Box.createVerticalStrut(5), 0, row++, 1, 1);
		
		GuiUtil.addComponent(content, label2, 0, row++, 1, 1);
		GuiUtil.addComponent(content, titleText, 0, row++, 1, 1);
		GuiUtil.addComponent(content, Box.createVerticalStrut(5), 0, row++, 1, 1);
		
		GuiUtil.addComponent(content, label3, 0, row++, 1, 1);
		GuiUtil.addComponent(content, reviewTextArea, 0, row++, 1, 8);
		row += 8;
		GuiUtil.addComponent(content, Box.createVerticalStrut(15), 0, row++, 1, 1);
        JPanel buttonP = new JPanel();
        buttonP.add(previewB);
		GuiUtil.addComponent(content, buttonP, 0, row++, 1, 1);		
	}
	
	public CreateReviewDialog getDialog() {
		return dialog;
	}

	public void setDialog(CreateReviewDialog d) {
		dialog = d;
	}
	
	public void setTitle(String title) {
		banner.setTitle(title);
	}
	
	public void setSubTitle(String subtitle) {
		banner.setSubtitle(subtitle);
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Testing");
		frame.setContentPane(new CreateReviewPanel());
		frame.setSize(800, 600);
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == previewB) {
			dialog.showPreview();
		}		
	}

	ResourceReview getResourceComment() {
		ResourceReview comment = new ResourceReview();
		comment.setAuthor(Main.getProperty("author"));
		comment.setCreatedDate(new Date());
		comment.setRate(itemRate.getSelectedIndex() * 2);
		comment.setTitle(titleText.getText().trim());
		comment.setComment(reviewTextArea.getText());
		
		return comment;
	}
	
	void setResource(ResourceReview comment) {
		itemRate.setSelectedIndex(comment.getRate()/2);
		titleText.setText(comment.getTitle());
		reviewTextArea.setText(comment.getComment());
	}

}
