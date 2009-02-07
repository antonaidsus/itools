package edu.ucla.loni.ccb.itools.view.review;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CommentTextArea extends JScrollPane {
	private JTextArea textArea = new JTextArea(10, 80);
	
	public CommentTextArea() {
		setViewportView(textArea);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		setMinimumSize(new Dimension(50,200));
	}
	
	public Dimension getPreferedSize() {
		return new Dimension(50,200);
	}
	
	public void setText(String str) {
		textArea.setText(str);
	}
	
	public String getText() {
		return textArea.getText();
	}

}
