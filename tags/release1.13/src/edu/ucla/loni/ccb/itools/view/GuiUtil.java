package edu.ucla.loni.ccb.itools.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

public class GuiUtil {
	public static final Color INPUT_COLOR = new Color(153, 204, 255);

	public static final Color TITLE_BACKGROUND = new Color(150, 150, 150);

	public static final Color HIGHLIGHT_COLOR = Color.green;

	public static final Color NRC_COLOR = Color.lightGray;

	/** specifying the color of the text. */
	public static Color TEXT_COLOR = Color.black;

	/** the color of the main canvas of pipeline. */
	public static Color BACKGROUND = Color.white;// Color.decode("0x000095");

	public static Font RADIOBUTTON_FONT = new Font("Arial", Font.PLAIN, 11);

	/** font of the text in a text component. */
	public static Font TOOL_TIP_FONT = new Font("Monospaced", Font.PLAIN, 12);
	/** the font of the label of a <code> JButton </code>. */
	public static Font BUTTON_FONT = new Font("Arial", Font.BOLD, 12);

	/** font of the text in a text component. */
	public static Font TEXT_FONT = new Font("Arial", Font.PLAIN, 11);

	/** font of the text in a text component. */
	public static Font TITLE_FONT = new Font("Sanserif", Font.BOLD, 12);

	/**
	 * Displays a message specifying by a string.
	 * 
	 * @param msg
	 *            a <code> String </code> specifying the detail information of a
	 *            message
	 */
	static private MessagePanel messagePane = new MessagePanel();

	public static void showMsg(String msg) {
		messagePane.setText(msg, null);
		// SwingUtilities.getWindowAncestor(MainFrame.getContent().getWindowAncestor())
		JOptionPane.showMessageDialog(MainFrame.getContent()
				.getWindowAncestor(), messagePane);
	}

	public static ImageIcon getIcon(String filename) {
		return new ImageIcon(GuiUtil.class.getResource(filename));
	}

	/** add the component with the label at left to the JPanel at row */
	public static void addInput2Pane(String label, Component c, JPanel p,
			int row) {
		JLabel jlabel = new JLabel(label);
		int a = GridBagConstraints.WEST, f = GridBagConstraints.HORIZONTAL;
		GuiUtil.addComponent(p, jlabel, 0, row, 1, 1, f, a, 0, 0);
		GuiUtil.addComponent(p, c, 1, row, 1, 1, f, a, 1, 1);
	}

	public static void addComponent(Container container, Component c, int x,
			int y, int w, int h) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 2, 2);
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		addComponent(container, c, gbc);
	}

	public static void addComponent(Container container, Component c, int x,
			int y, int w, int h, int fill, int anchor, int wx, int wy) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 2, 2);
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.fill = fill;
		gbc.anchor = anchor;
		gbc.weightx = wx;
		gbc.weighty = wy;
		addComponent(container, c, gbc);
	}

	public static void addComponent(Container container, Component c,
			GridBagConstraints gbc) {
		container.add(c, gbc);
	}

	// public static void centerOnScreen(Window window) {
	// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	// Dimension size = window.getSize();
	// window.setLocation(
	// (screenSize.width - size.width) / 2,
	// (screenSize.height - size.height) / 2);
	// }
	public static void modifyUIValues() {
		ToolTipManager.sharedInstance().setDismissDelay(6000);
		UIManager.put("OptionPane.minimumSize", new Dimension(100, 120));
		UIManager.put("ComboBox.font", TEXT_FONT);
		UIManager.put("Button.font", BUTTON_FONT);
		UIManager.put("Label.font", TEXT_FONT);
		UIManager.put("CheckBox.font", RADIOBUTTON_FONT);

		// UIManager.put("CheckBox.foreground", INPUT_COLOR);
		// UIManager.put("RadioButton.foreground", INPUT_COLOR);

		UIManager.put("TextField.font", TEXT_FONT);
		UIManager.put("TextArea.font", TEXT_FONT);
		UIManager.put("EditorPane.font", TEXT_FONT);
		UIManager.put("TitledBorder.font", TITLE_FONT);
		UIManager.put("ToolTip.font", TOOL_TIP_FONT);
		UIManager.put("RadioButton.font", RADIOBUTTON_FONT);
		UIManager.put("TextField.margin", new Insets(0, 0, 0, 0));
		UIManager.put("RadioButton.margin", new Insets(0, 0, 0, 0));
		UIManager.put("CheckBox.margin", new Insets(0, 0, 0, 0));
		UIManager.put("Button.margin", new Insets(0, 14, 0, 14));

		// UIManager.put("Panel.background", BACKGROUND);
		// UIManager.put("CheckBox.background", BACKGROUND);
		UIManager.put("RadioButton.background", BACKGROUND);
		// UIManager.put("TextField.background", BACKGROUND);
		UIManager.put("ComboBox.buttonBackground", INPUT_COLOR);
		UIManager.put("ComboBox.background", BACKGROUND);
		UIManager.put("ComboBox.disabledBackground", BACKGROUND);
		UIManager.put("ComboBox.background", INPUT_COLOR);
		UIManager.put("Button.background", INPUT_COLOR);
		UIManager.put("TitledBorder.titleColor", Color.gray);
	}

}
