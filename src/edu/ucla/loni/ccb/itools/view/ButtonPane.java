/* Created on Oct 18, 2004 $Id$*/
package edu.ucla.loni.ccb.itools.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


/**
 * This class provides a convient way to create Buttons.
 * 
 * @author <A HREF="mailto:qma@loni.ucla.edu">Jeff Ma </A>
 */
public class ButtonPane extends JPanel {
    /** for serialization*/
    private static final long serialVersionUID = 3690479104088880696L;
    /** the button should be align to the left/top, or right/ bottom*/
    private boolean mAlignment2Head = true;
    
    /** Buttons align horizonal if true */
    private boolean mHorizontal = true;
    
    /** array which contains all buttons of the instance*/
    private JButton[] mButtons = null;
    
    /** if true then the buttons will draw as same size*/
    private boolean mButtonSameSize = false;

    /**
     * Constructs a <code> ButtonPane </code>  whose buttons labeled
     * as <code>labels</code>
     *
     * @param labels the labels of the buttons
     */
    public ButtonPane(String[] labels) {
        mButtons = new JButton[labels.length];
        for (int i = 0; i < labels.length; i++) {
            mButtons[i] = new JButton(labels[i]);
            mButtons[i].setName(labels[i]);
        }
    }
    
    /**
     * Constructs a <code> ButtonPane </code>  whose buttons labeled
     * as <code>labels</code>
     *
     * @param labels the labels of the buttons
     * @param tips texts for tips of the buttons
     */
    public ButtonPane(String[] labels, String[] tips) {
        this(labels);
        for (int i = 0; i < labels.length; i++) {
            mButtons[i].setToolTipText(tips[i]);
        }
    }

    
    /**
     * Sets wheather the  buttons be alignment.
     * @param b if true the buttons be align to left/top.
     */    
    public void setAlign2Head( boolean b) {
        mAlignment2Head = b;
    }

    /**
     * Set wheather the buttons align along the horizontal.
     * @param b if false the button align along vertical.
     */
    public void setHorizontal(boolean b) {
        mHorizontal = b;
    }
   
    /**
     * sets a EmptyBorder around the panel.
     * @param t pixels at top
     * @param l pixels at left
     * @param b pixels at bottom
     * @param r pixels at right
     */
    public void setBorder(int t, int l, int b, int r) {
        super.setBorder(BorderFactory.createEmptyBorder(t, l, b, r));
    }

    /**
     * Adds the same actionListener into the buttons.
     *
     * @param listener the action listener of the buttons
     */
    public void addActionListener(ActionListener listener) {
        for (int i = 0; i < mButtons.length; i++) {
            mButtons[i].addActionListener(listener);
        }
    }

    /**
     * Sets weather the buttons should be drawn at same size.
     * @param b a boolean balue.
     */
    public void setButtonSameSize(boolean b) {
        mButtonSameSize = b;
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#addNotify()
     */
    public void addNotify() {
        super.addNotify();
        LayoutManager buttonlayout = null;
         if (mHorizontal) {
        	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        	if (mButtonSameSize)
        		buttonlayout = new GridLayout(1, mButtons.length, 4,4);
        	else 
        		buttonlayout = new FlowLayout(FlowLayout.LEFT, 4, 4);
        } else {
        	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        	buttonlayout = new GridLayout(mButtons.length, 1, 4,4);
        }
        		
        JPanel grid = new JPanel(buttonlayout);
        for (int i = 0; i < mButtons.length; i++) {
            grid.add(mButtons[i]);
        }
        if (!mAlignment2Head) add(Box.createGlue());
        add(grid);
        if (mAlignment2Head) add(Box.createGlue());
        grid.setMaximumSize(grid.getPreferredSize());
    }
    
    /**
     * Returns the button specified by its index in String array lables
     *
     * @param index the index of the button in the array
     * @return the button specified by the index, or null if index is not legal.
     */
    public JButton getButton(int index) { 
        if (index < 0 || index >= mButtons.length) return null;
        return mButtons[index];
    }
    
    /**
     * Returns the button specified by its name.
     *
     * @param name text of the button in the array.
     * @return the button whose text is name, or null if no button found.
     */
    public JButton getButton(String name) {
        for (int i = 0; i < mButtons.length; i++) {
            if (mButtons[i].getName().equals(name)) return mButtons[i];
        }
        return null;
    }
}
