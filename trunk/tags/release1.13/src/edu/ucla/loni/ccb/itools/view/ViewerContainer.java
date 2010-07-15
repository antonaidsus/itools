package edu.ucla.loni.ccb.itools.view;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import edu.ucla.loni.ccb.itools.view.review.CommentPane;
import edu.ucla.loni.ccb.itools.view.table.DataTableModel;
import edu.ucla.loni.ccb.itools.view.table.NcbcTable;
import edu.ucla.loni.ccb.itools.view.table.NcbcTableSorter;

/**
 * This class shows the viewer of the NcbcResource. It basically shows either a
 * Table or a SpreadSheetViewer.
 * 
 * @author Qunfei Ma
 * 
 */
public class ViewerContainer extends JPanel {
	static final String WELCOM_TEXT = "welcome";
	static final String SHEET_TEXT = "sheet";
	static final String TABLE_TEXT = "table";
	static final String COMMENT_TEXT = "comment";

	private DataTableModel tableModel = new DataTableModel();
	private NcbcTableSorter sorter = new NcbcTableSorter(tableModel);
	private JTable table = new NcbcTable(sorter);

	private SpreadSheetViewer sheetPanel = new SpreadSheetViewer();

	private JScrollPane tablePaneScroll = new WaterMarkScroll(table);
	private JScrollPane sheetPaneScroll = new JScrollPane(sheetPanel);
	private WelcomePane welcomePane = new WelcomePane();
	private CommentPane commentPane = new CommentPane();
	private CardLayout layout = new CardLayout();

	public ViewerContainer() {
		setLayout(layout);
		sorter.setTable(table);
		add(welcomePane, WELCOM_TEXT);
		add(sheetPaneScroll, SHEET_TEXT);
		add(tablePaneScroll, TABLE_TEXT);
		add(commentPane, COMMENT_TEXT);
		tablePaneScroll.getViewport().setBackground(Color.white);
	}

	/**
	 * Display single Resource in spreadsheet format.
	 * 
	 * @param rsc
	 */
	public void setData(ResourceAndViewNode rscAndNode) {
		sheetPanel.setData(rscAndNode);
		sheetPanel.validate();
		display(SHEET_TEXT);
	}

	public void setData(List<ResourceAndViewNode> data) {
		tableModel.setData(data);
		sorter.setTableModel(tableModel);
		display(TABLE_TEXT);
	}

	public void display(String name) {
		layout.show(this, name);
	}

	public JTable getTableViewer() {
		return table;
	}

	public CommentPane getCommentViewer() {
		return commentPane;
	}

	Image image = MainFrame.ITOOLS_WATERMARK.getImage();

	public class WelcomePane extends JPanel {
		public Font FONT = new Font("Helvetica", Font.PLAIN, 36);
		String str = "Welcome to NCBC iTools";

		public WelcomePane() {
			setBackground(Color.WHITE);
		}

		public void paint(Graphics g) {
			// First draw the background image - tiled
			g.drawImage(image, 0, 0, getWidth(), getHeight(), null, null);
			g.setFont(FONT);

			int w = SwingUtilities.computeStringWidth(g.getFontMetrics(), str);
			g.drawString(str, (getWidth() - w) / 2, getHeight() / 2);
		}
	}

	class WaterMarkViewPort extends JViewport {
		WaterMarkViewPort() {
			setOpaque(false);
		}

		public void paint(Graphics g) {
			// First draw the background image - tiled
			g.drawImage(image, 0, 0, getWidth(), getHeight(), null, null);
			// Dimension d = getSize();
			// for( int x = 0; x < d.width; x += image.getIconWidth() )
			// for( int y = 0; y < d.height; y += image.getIconHeight() )
			// g.drawImage( image.getImage(), x, y, null, null );
			// // Now let the regular paint code do it's work
			super.paint(g);
		}
	}

	class WaterMarkScroll extends JScrollPane {
		WaterMarkScroll(JTable comp) {
			JViewport vport = new WaterMarkViewPort();
			setViewport(vport);
			vport.setView(comp);
		}

	}
}
