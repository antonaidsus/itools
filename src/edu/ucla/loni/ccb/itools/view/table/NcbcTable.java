package edu.ucla.loni.ccb.itools.view.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.view.MainFrame;
import edu.ucla.loni.ccb.itools.view.table.tooltip.MultiLineToolTip;

public class NcbcTable extends JTable {
	private static final long serialVersionUID = 1L;
	private final class MouseAdapterExtension  implements MouseListener,MouseMotionListener {
		private int draggedRowIndex = 0;
		boolean dragged = false;
		private int draggedColIndex;

		public void mouseDragged(MouseEvent e) {
            dragged = true;
		}
		

		public void mousePressed(MouseEvent e) {
			draggedRowIndex = getSelectedRow();
			draggedColIndex = columnAtPoint(e.getPoint());
		}


		public void mouseReleased(MouseEvent e) {	
			if (!dragged) {
				return;
			}
			int colAtPoint = columnAtPoint(e.getPoint());
			if (colAtPoint != draggedColIndex ) {
				return;
			}
			int rowAtPoint = rowAtPoint(e.getPoint());

			NcbcTableSorter model = ((NcbcTableSorter) getModel());
//			model.setDraggedRow(draggedRowIndex);
//			model.setDroppedRow(rowAtPoint);
//			model.setDragged(true);
			model.rowDragged(draggedRowIndex, rowAtPoint);

			model.fireTableDataChanged();
//			System.out.println("draged=" + dragged + ", and index=" + draggedRowIndex + ", realease at ="+ rowAtPoint);
			dragged = false;

			draggedRowIndex = 0;
			draggedColIndex = 0;
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() != 2) return;
			DataTableModel model = (DataTableModel) ((NcbcTableSorter) getModel()).getTableModel();
			String cn=getColumnName(getSelectedColumn());
			if (cn.equals("Name")||cn.equals("URL")) {
				NcbcResource rsc = model.getValueAt(getSelectedRow());
				MainFrame.getContent().expandNode(rsc);					
			}
		}


		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}


		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}

	MultilineCellRender render = new MultilineCellRender();
	public NcbcTable(TableModel model){
		super(model);
		setRowHeight(getRowHeight() *2);
		setOpaque(false);
		setMinimumSize(new Dimension(700, 600));
		MouseAdapterExtension mouseAdapterExtension = new MouseAdapterExtension();
		addMouseListener(mouseAdapterExtension);
		addMouseMotionListener(mouseAdapterExtension);
	}

	  public JToolTip createToolTip() {
	        MultiLineToolTip tip = new MultiLineToolTip();
	        tip.setComponent(this);
	        return tip;
	   }


	public String getToolTipText(MouseEvent event)
	{
		int row = rowAtPoint( event.getPoint() );
		int col = columnAtPoint( event.getPoint() );
		if (row < 0 || col < 0) return null;

		Object o = getValueAt(row,col);
		if( o == null )
			return null;
		String str =o.toString();
		if( str.equals("") ) {
			return null;
		} 
		
		return  str;
	}

	public Point getToolTipLocation(MouseEvent event)
	{
		int row = rowAtPoint( event.getPoint() );
		int col = columnAtPoint( event.getPoint() );
		if (row < 0 || col < 0) return null;
		Object o = getValueAt(row,col);
		if( o == null )
			return null;
		if( o.toString().equals("") )
			return null;
		Point pt = getCellRect(row, col, true).getLocation();
		pt.translate(-1,-2);
		return pt;
	}
	public TableCellRenderer getCellRenderer(int row, int column) {
        if (getColumnName(column).equals("Description") ||
        		getColumnName(column).equals("Ontology")	) {
            return render;
        }
        // else...
        return super.getCellRenderer(row, column);
    }
	
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) 
	{
		Component c = super.prepareRenderer( renderer, row, column);
		// We want renderer component to be transparent so background image is visible
		if( c instanceof JComponent )
			((JComponent)c).setOpaque(false);
		return c;
	}

	
	Image image = MainFrame.ITOOLS_WATERMARK.getImage();
	int minH = 600;

//	public void paint( Graphics g ) {
//		g.drawImage( image, 0, 0, getWidth(), minH, null, null );
//		// First draw the background image - tiled 
//		//g.drawImage( image, 0, 0, getWidth(), image.getHeight(this), null, null );
//		
//		Dimension d = getSize();
//		//for( int x = 0; x < d.width; x += image.getIconWidth() )
//			for( int y = minH; y < d.height; y += image.getHeight(this)/*getIconHeight()*/ )
//				g.drawImage( image, 0, y, null, null );
//		// Now let the regular paint code do it's work
//		super.paint(g);
//	}


}
