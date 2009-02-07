/*
COPYRIGHT NOTICE
Copyright (c) 2005  Jeff Q. Ma and Arthur W. Toga

See README.license for license notices.
 */

package edu.ucla.loni.ccb.itools.view;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Displays textual messages.
 * 
 * @author <A HREF="mailto:qma@ucla.edu">Jeff Ma </A>
 * @version April 8, 2005
 */

public class MessagePanel extends JScrollPane {
	private static final long serialVersionUID = 1L;
	/** The JTeextpane to display the message */
	private JTextPane _pane = new JTextPane();

	/**
	 * Constructs a <code> MessagePanel with a scrollable JTextPane in it.
	 */
	public MessagePanel() {
		setViewportView(_pane);
		_pane.setEditable(false);
	}

	/*
	 * (non-Javadoc) Overridden methods
	 * 
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return new Dimension(500, 300);
	}

	/**
	 * To display the message with red fonts.
	 * 
	 * @param msg
	 *            the error message to be displayed.
	 */
	public final void error(String msg) {
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setForeground(attrs, Color.red);
		showMsg(msg, attrs);
	}

	/**
	 * To display the message with yellow fonts.
	 * 
	 * @param msg
	 *            the warning message to be displayed.
	 */
	public final void warn(String msg) {
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setForeground(attrs, Color.blue);
		showMsg(msg, attrs);
	}

	/**
	 * Display a message using normal color</code>.
	 * 
	 * @param msg
	 *            the message to be displayed.
	 */
	public final void msg(String msg) {
		showMsg(msg, null);
	}

	/**
	 * Shows a text message using the specified AttributeSet.
	 * 
	 * @param msg
	 *            the text to be displayed.
	 * @param attrs
	 *            the attributes associate with the text.
	 */
	public final void showMsg(final String msg, final AttributeSet attrs) {
		try {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						Document doc = _pane.getDocument();
						doc.insertString(doc.getLength(), msg, attrs);
					} catch (BadLocationException ex) {
						// LOGGER.error(ex, ex);
					}
				}
			});
		} catch (Exception ex) {
			// LOGGER.error(ex, ex);
		}
	}

	public void setText(String msg, AttributeSet attrs) {
		_pane.setText("");
		showMsg(msg, attrs);
	}
}