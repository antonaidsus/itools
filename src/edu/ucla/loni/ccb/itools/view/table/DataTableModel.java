/**
 * 
 */
package edu.ucla.loni.ccb.itools.view.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

public class DataTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private ArrayList<NcbcResource> resources = new ArrayList<NcbcResource>(0);

	/**
	 * This method returns all the Columns which are shown at the table.
	 * 
	 * @return
	 */
	// public static String[] getDisplayNames() {
	// List rv = new LinkedList();
	// for ( Iterator i = ALLKEYS.iterator(); i.hasNext();) {
	// String s = (String) i.next();
	// if (!unshownKeys.contains(s)){
	// rv.add(s);
	// }
	// }
	// return (String[]) rv.toArray(new String[0]);
	// }
	/**
	 * @param name
	 * @return true if the Field Name is now being displayed.
	 */
	// public static boolean isDisplaying(String name) {
	// return !hidedColumns.keySet().contains(name);
	// }
	// public TableColumn getHidedColumn(String name) {
	// return (TableColumn) hidedColumns.get(name);
	// }
	//
	// public Map getHidedColumns() {
	// return hidedColumns;
	// }
	public DataTableModel() {

	}

	public DataTableModel(List<? extends NcbcResource> data) {
		setData(data);
	}

	public void setData(List<? extends NcbcResource> data) {
		resources = new ArrayList<NcbcResource>(data);
	}
	
	public ArrayList<NcbcResource> getData() {
		return resources;
	}

	public String getColumnName(int index) {
		return Descriptor.getAllFieldNames()[index];
	}

	public int getColumnCount() {
		return Descriptor.getAllFieldNames().length;
	}

	public int getRowCount() {
		return resources.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		NcbcResource r = (NcbcResource) resources.get(rowIndex);
		String v = r.getProperty(getColumnName(columnIndex));
		return v;
	}

	public NcbcResource getValueAt(int rowIndex) {
		return (NcbcResource) resources.get(rowIndex);
	}

	public Class<?> getColumnClass(int c) {
		if (getColumnName(c).equals("Description")) {
			return JTextArea.class;
		}
		return Object.class;
	}
}