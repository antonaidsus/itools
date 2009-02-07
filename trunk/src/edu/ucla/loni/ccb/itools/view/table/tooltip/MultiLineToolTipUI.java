package edu.ucla.loni.ccb.itools.view.table.tooltip;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;

public class MultiLineToolTipUI extends MetalToolTipUI {
	  private String[] strs;
	  private int maxWidth = 0;

	  public void paint(Graphics g, JComponent c) {
	    FontMetrics metrics = g.getFontMetrics(); //Toolkit.getDefaultToolkit().getFontMetrics(g.getFont());
	    Dimension size = c.getSize();
	    g.setColor(c.getBackground());
	    g.fillRect(0, 0, size.width, size.height);
	    g.setColor(c.getForeground());
	    if (strs != null) {
	      for (int i=0;i<strs.length;i++) {
	        g.drawString(strs[i], 3, (metrics.getHeight()) * (i+1));
	      }
	    }
	  }

	  public Dimension getPreferredSize(JComponent c) {
	    FontMetrics metrics = c.getFontMetrics(c.getFont());//Toolkit.getDefaultToolkit().getFontMetrics(c.getFont());
	    String tipText = ((JToolTip)c).getTipText();
	    if (tipText == null) {
	      tipText = "not tip";
	    }
	    if (tipText.length()< 70) {
	    	strs = new String[1];
	    	strs[0] = tipText;
	    } else {
//	    	//if the line is too long break it to many lines
	    	StringBuffer buf = new StringBuffer();
	    	int j = 0;
	    	for ( int i = 60; i < tipText.length(); i += 60) {
	    		int index = tipText.indexOf(" ", i);
	    		if (index == -1) { index = tipText.length();}
	    		buf.append(tipText.substring(j, index)+ "\n");
	    		j = index;
	    	}
	    	if (j < tipText.length()) buf.append(tipText.substring(j,tipText.length()));
	    	
//	    	System.err.println(buf.toString());
//	    	System.out.println(tipText);
//
	    	strs = buf.toString().split("\n");
	    }
	    int height = metrics.getHeight() * strs.length;
	    
	    int longest = strs[0].length();
	    int lll = 0;
	    for (int i = 1; i < strs.length; i++) {
			if (strs[i].length() > longest) {
				lll = i;
				longest = strs[i].length();
			}
		}
	    maxWidth = SwingUtilities.computeStringWidth(metrics,strs[lll]);
	    return new Dimension(maxWidth + 6, height + 4);
	  }
	}

