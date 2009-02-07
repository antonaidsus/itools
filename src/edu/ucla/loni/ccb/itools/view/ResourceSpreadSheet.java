package edu.ucla.loni.ccb.itools.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * This class simply shows the value or gets the value of a NcbcResource to or
 * from the Panel.
 * 
 * @author Qunfei Ma
 * 
 */
public class ResourceSpreadSheet extends JPanel {
	/** Logger for this class */
	public static final Logger LOGGER = Logger
			.getLogger(ResourceSpreadSheet.class);
	private JPanel required = new JPanel(new GridBagLayout());
	private JPanel optional = new JPanel(new GridBagLayout());
	private JLabel othersLabel = new JLabel("The following are nonstandard");

	public ResourceSpreadSheet(boolean editable) {
		setLayout(new BorderLayout());
		setupGui();
	}

	private void setupGui() {
		setupRequired();
		// if (!editable) {
		// setupOptional();
		// }
		add(required, BorderLayout.CENTER);
		add(optional, BorderLayout.SOUTH);
	}

	private void setupRequired() {
		int row = 0;
		Iterator i = Descriptor.getRequiredDescriptorNames().iterator();
		while (i.hasNext()) {
			String name = (String) i.next();
			Descriptor des = Descriptor.getDescriptor(name);
			JComponent c = null;
			if (des == null) {
				LOGGER.error("No Descriptor defined for: " + name);
				continue;
			}
			String av = des.getAvailableValues();
			if (av != null && av.trim().length() != 0) {
				String[] items = av.split(",");
				for (int j = 0; j < items.length; j++) {
					items[j] = items[j].trim();
				}
				c = new MyComboBox(items);
			} else if (des.isSingleline()) {
				c = new MyTextField();
			} else {
				c = new MyTextArea();
			}
			c.setName(name);
			GuiUtil.addInput2Pane(name, c, required, row++);
		}
	}

	private void setupOptional(NcbcResource rsc) {
		optional.removeAll();
		int row = 0;
		Set others = Descriptor.getOtherDescriptorNames(rsc);
		if (others.size() > 0) {
			GuiUtil.addInput2Pane("", Box.createVerticalStrut(10), optional,
					row++);
			GuiUtil.addInput2Pane("", othersLabel, optional, row++);
			GuiUtil.addInput2Pane("", Box.createVerticalStrut(8), optional,
					row++);
		}
		Iterator i = others.iterator();
		while (i.hasNext()) {
			String name = (String) i.next();
			if (name.equals(NcbcResource.SANDBOX)) {
				continue;
			}
			MyTextField t = new MyTextField();
			GuiUtil.addInput2Pane(name, t, optional, row++);
			t.setName(name);
		}
	}

	/**
	 * Display the value of the rsc to this Panel.
	 * 
	 * @param rsc
	 */
	public void displayResource(NcbcResource rsc) {
		display2Panel(required, rsc);
		setupOptional(rsc);
		revalidate();
		display2Panel(optional, rsc);
		// Component[] components = this.getComponents();
		// for (int i = 0; i < components.length; i++) {
		// if (components[i] instanceof ValueText) {
		// String name = components[i].getName();
		// ((ValueText)components[i]).setText(rsc.getProperty(name));
		// }
		// }
	}

	private void display2Panel(JPanel pane, NcbcResource rsc) {
		Component[] components = pane.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof ValueText) {
				String name = components[i].getName();
				((ValueText) components[i]).setText(rsc.getProperty(name));
			}
		}
	}

	/**
	 * Creates a NcbcResource and sets its values from the Panel.
	 * 
	 * @return
	 */
	public NcbcResource getResource() {
		NcbcResource ncbcr = new NcbcResource();
		getValueFromPane(required, ncbcr);
		getValueFromPane(optional, ncbcr);
		// Component[] components = this.getComponents();
		// for (int i = 0; i < components.length; i++) {
		// if (components[i] instanceof ValueText) {
		// String name = components[i].getName();
		// String value = ((ValueText)components[i]).getText();
		// if (name != null && value != null) {
		// ncbcr.putProperty(name, value);
		// }
		// }
		// }
		return ncbcr;
	}

	private void getValueFromPane(JPanel pane, NcbcResource rsc) {
		Component[] components = pane.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof ValueText) {
				String name = components[i].getName();
				String value = ((ValueText) components[i]).getText();
				if (name != null && value != null) {
					rsc.putProperty(name, value);
				}
			}
		}
	}

	public void setValue(String name, String value) {
		ValueText vs = (ValueText) getByName(name);
		if (vs != null) {
			vs.setText(value);
		} else {
			LOGGER.warn("No component found by name:" + name);
		}
	}

	public Component getByName(String name) {
		Component[] components = (Component[]) ArrayUtils.addAll(required
				.getComponents(), optional.getComponents());
		// Component[] components = this.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof ValueText
					&& components[i].getName().equals(name)) {
				return components[i];
			}
		}
		return null;
	}

	interface ValueText {
		void setText(String text);

		String getText();
	}

	public class MyTextField extends JTextField implements ValueText {
		MyTextField() {
			setDocument(new AutoCompleteDocument(this));
		}

		MyTextField(String text) {
			this();
			setText(text);
		}

		public void setName(String name) {
			super.setName(name);
			// ((AutoCompleteDocument)getDocument()).addDictionaryEntry(Arrays.asList(
			// aDictionary ));
			((AutoCompleteDocument) getDocument())
					.addDictionaryEntry(Dictionary.getValues(name));
		}
	}

	public static class MyTextArea extends JScrollPane implements ValueText {
		private static final long serialVersionUID = 1L;
		JTextArea textarea = new JTextArea(6, 30);

		MyTextArea() {
			textarea.setPreferredSize(new Dimension(40, 30));
			textarea.setLineWrap(true);
			textarea.setWrapStyleWord(true);
			setOpaque(true);
			// textarea.setEnabled(editable);
			setViewportView(textarea);
		}

		MyTextArea(String text) {
			this();
			textarea.setText(text);
		}

		public void setText(String text) {
			textarea.setText(text);
		}

		public String getText() {
			return textarea.getText();
		}

		public void setEditable(boolean b) {
			textarea.setEditable(b);
		}
	}

	public class MyComboBox extends JComboBox implements ValueText {
		public MyComboBox(Object[] items) {
			super(items);
			// setEnabled(editable);
		}

		public void setText(String text) {
			setSelectedItem(text);
		}

		public String getText() {
			return (String) getSelectedItem();
		}
	}
}
