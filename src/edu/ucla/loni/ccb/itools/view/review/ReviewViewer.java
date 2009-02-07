package edu.ucla.loni.ccb.itools.view.review;

import java.awt.AWTException;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.l2fprod.common.swing.BannerPanel;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.Messages;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.ResourceReview;
import edu.ucla.loni.ccb.itools.view.GuiUtil;
import edu.ucla.loni.ccb.itools.view.MainFrame;

/**
 * 
 * This class shows single Comment.
 * 
 * @author Jeff Ma
 *
 */
public class ReviewViewer extends JPanel implements ActionListener {
	BannerPanel banner = new BannerPanel();
//    public JLabel title = new JLabel(Messages.getString("review.viewer.title"));
//    private JLabel summary = new JLabel(); 

    private JLabel header_rate = new JLabel();
    private JLabel header_title = new JLabel();
    private JLabel header_date = new JLabel();
    private JLabel header_reviewer = new JLabel();
    
    CommentTextArea textArea = new CommentTextArea();
    private JButton helpful_yes = new JButton(Messages.getString("review.viewer.yes"));
    private JButton helpful_no = new JButton(Messages.getString("review.viewer.no"));
    
    ResourceReview comment;
    boolean preview = false;
    SummaryPanel summaryPanel;
    
    public ReviewViewer(SummaryPanel summaryPanel) {
    	this(summaryPanel, false);
    }
    
    public ReviewViewer(SummaryPanel summaryPanel, boolean preview) {
    	this.preview = preview;
    	this.summaryPanel = summaryPanel;
    	try {
			setupGui();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	private void setupGui() throws AWTException {
		setLayout(new GridBagLayout());
//		Font font = title.getFont();
//		font.deriveFont(Font.BOLD, font.getSize() + 5.0f);
//		title.setFont(font);
		int row = 0;
		
//		GuiUtil.addComponent(this, title, 0, row, 1, 1);
//
//		GuiUtil.addComponent(this, Box.createVerticalStrut(5), 0, ++row, 1, 1);
//		GuiUtil.addComponent(this, helpful_summary, 0, ++row, 1, 1);
		GuiUtil.addComponent(this, banner, 0, row, 1, 1);
		banner.setIcon(MainFrame.ITOOLS_ICON);
		GuiUtil.addComponent(this, Box.createVerticalStrut(5), 0, ++row, 1, 1);
		
		JPanel pane = new JPanel(new FlowLayout(3));
		pane.add(header_rate);
		pane.add(header_title);
		pane.add(Box.createVerticalStrut(10));
		pane.add(header_date);
		header_date.setFont(header_date.getFont().deriveFont(Font.PLAIN));
		
		GuiUtil.addComponent(this, pane, 0, ++row, 1, 1);
		GuiUtil.addComponent(this, header_reviewer, 0, ++row, 1, 1);
		GuiUtil.addComponent(this, Box.createVerticalStrut(5), 0, ++row, 1, 1);
		
		GuiUtil.addComponent(this, textArea, 0, ++row, 1, 1);
		
		if (!preview) {
			pane = new JPanel(new FlowLayout(3));
			pane.add(new JLabel(Messages.getString("review.viewer.comment")));
			pane.add(helpful_yes);
			pane.add(helpful_no);
			helpful_yes.addActionListener(this);
			helpful_no.addActionListener(this);
			GuiUtil.addComponent(this, Box.createVerticalStrut(5), 0, ++row, 1, 1);
			GuiUtil.addComponent(this, pane, 0, ++row, 1, 1);
		}
	}
	
	public void setValue(ResourceReview comment) {
		this.comment = comment;
		
		if (preview) {
			banner.setTitle(Messages.getString("review.previewer.title", comment.getResourceName()));
			banner.setSubtitle(Messages.getString("review.previewer.summary"));
		} else {
			banner.setTitle(Messages.getString("review.viewer.title", comment.getResourceName()));
			banner.setSubtitle(Messages.getString("review.viewer.summary", comment.getVote()));
		}
		
		header_rate.setIcon(CommentPane.getIcon(comment.getRate()));
		header_title.setText(comment.getTitle());
		header_date.setText(comment.getCreatedDateAsString());
		header_reviewer.setText(Messages.getString("review.viewer.reviewer",
				comment.getAuthor()));
		textArea.setText(comment.getComment());
	}
	
	public ResourceReview getValue() {
		return comment;
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o == helpful_yes) {
			comment.setYesvote(comment.getYesvote()+1);
			comment.setTotalvote(comment.getTotalvote()+1);
		} else if (o == helpful_no) {
			comment.setTotalvote(comment.getTotalvote()+1);
		}
		DaoFactory.getDaoFactory().getReviewDao().update(comment);
	}
}
