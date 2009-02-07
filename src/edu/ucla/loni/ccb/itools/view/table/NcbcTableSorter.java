package edu.ucla.loni.ccb.itools.view.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

public class NcbcTableSorter extends TableSorter {
	private static final long serialVersionUID = 1L;
	/* having all fields as one item, used to hide/unhide column */
	private JPopupMenu popup = new JPopupMenu();
	/* the reference of the table */
	private JTable table;
	/* current unhided column, used when unhiding */
	private Map<String, TableColumn> hidedColumns = new HashMap<String, TableColumn>();
	private DataTableModel model;

	public NcbcTableSorter(DataTableModel tableModel) {
		super(tableModel);
		model = tableModel;
	}

	public void setTable(JTable table) {
		this.table = table;
		setTableHeader(table.getTableHeader());
	}

	public void setTableModel(DataTableModel tableModel) {
		super.setTableModel(tableModel);
		model = tableModel;
		hidedColumns.clear();

		// maybe to earlier
		for (String name : Descriptor.getUnshownKeys()) {
			TableColumn aColumn = table.getColumn(name);
			table.getColumnModel().removeColumn(aColumn);
			hidedColumns.put(name, aColumn);
		}
	}

	protected void doPopup(MouseEvent e) {
		String[] str = Descriptor.getAllFieldNames();
		if (popup.getComponentCount() != str.length) {
			popup.removeAll();
			for (int i = 0; i < str.length; i++) {
				addItem(str[i]);
			}
		}
		popup.show((Component) e.getSource(), e.getX(), e.getY());
	}

	private void addItem(String name) {
		boolean b = Descriptor.getUnshownKeys().contains(name);
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(name, !b);
		item.addActionListener(l);
		popup.add(item);
	}

	private ActionListener l = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JCheckBoxMenuItem o = (JCheckBoxMenuItem) e.getSource();
			dealColumnNamed(o.getActionCommand(), o.isSelected());
		}
	};

	private void dealColumnNamed(String name, boolean display) {
		TableColumn aColumn = null;
		if (display) {
			aColumn = /* dataTableModel. */getHidedColumn(name);
			if (aColumn == null)
				throw new RuntimeException("Strange;");
			table.getColumnModel().addColumn(aColumn);
			/* dataTableModel. */getHidedColumns().remove(name);
			Descriptor.getUnshownKeys().remove(name);
		} else {
			aColumn = table.getColumn(name);
			table.getColumnModel().removeColumn(aColumn);
			/* dataTableModel. */getHidedColumns().put(name, aColumn);
			Descriptor.getUnshownKeys().add(name);
		}
	}

	public TableColumn getHidedColumn(String name) {
		return (TableColumn) hidedColumns.get(name);
	}

	public Map<String, TableColumn> getHidedColumns() {
		return hidedColumns;
	}
	
//	private boolean dragged;
//	private int draggedRow = 0;
//	private int droppedRow = 0;
//
//    private int modifyDragged(int row) {
//		if (draggedRow < droppedRow) {
//			if (row < draggedRow || row > droppedRow) {
//				return row;
//			} else if (row == droppedRow) {
//				return draggedRow;
//			} else {
//				return row +1;
//			}
//		} else {
//			if (row <= droppedRow || row > draggedRow) {
//				return row;
//			} else if (row == droppedRow + 1) {
//				return draggedRow;
//			} else {
//				return row - 1;
//			}
//		}
//	}

    public void rowDragged(int draggedRow, int droppedRow ) {
    		NcbcResource element = model.getData().remove(draggedRow); 
    		int index = draggedRow < droppedRow ? droppedRow : (droppedRow +1);
    		model.getData().add(index, element);    		 	
    }

}
