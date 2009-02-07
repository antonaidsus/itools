package edu.ucla.loni.ccb.itools.view.table;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import edu.ucla.loni.ccb.itools.view.table.tooltip.MultiLineToolTip;

public class MultilineCellRender extends JTextArea implements TableCellRenderer {
	  public MultilineCellRender() {
		super(4, 20);
	    setLineWrap(true);
	    setWrapStyleWord(true);
	    setOpaque(true);
	  }

	  public Component getTableCellRendererComponent(JTable table, Object value,
	               boolean isSelected, boolean hasFocus, int row, int column) {
	    if (isSelected) {
	      setForeground(table.getSelectionForeground());
	      setBackground(table.getSelectionBackground());
	    } else {
	      setForeground(table.getForeground());
	      setBackground(table.getBackground());
	    }
	    setFont(table.getFont());
//	    if (hasFocus) {
//	      setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
//	      if (table.isCellEditable(row, column)) {
//	        setForeground( UIManager.getColor("Table.focusCellForeground") );
//	        setBackground( UIManager.getColor("Table.focusCellBackground") );
//	      }
//	    } else {
//	      setBorder(new EmptyBorder(1, 2, 1, 2));
//	    }
	    setText((value == null) ? "" : value.toString());
	    return this;//new JScrollPane(this);
	  }
	}

