package edu.ucla.loni.ccb.itools.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.Util;

/**
 * This class gives GUI used to search specific Resources.
 * 
 * @author Jeff Ma
 * 
 */
public class SearchPanel extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(SearchPanel.class);

	JTextField searchTextfield = new JTextField(15);
	JComboBox searchCategory = new JComboBox(Descriptor.getAllFieldNames());
	JCheckBox searchAll = new JCheckBox("All Centers");
	JButton searchButton = new JButton("Search");
	AutoCompleteDocument autoCompleteDocument = new AutoCompleteDocument(
			searchTextfield);
	private StandardResourceDisplayer displayer;

	public SearchPanel(StandardResourceDisplayer displayer) {
		this.displayer = displayer;
		setUpGui();
	}

	private void setUpGui() {
		add(new JLabel("Key Word "));
		add(searchTextfield);
		searchTextfield.setDocument(autoCompleteDocument);
		add(Box.createHorizontalStrut(5));
		add(new JLabel("Category "));
		add(searchCategory);
		add(Box.createHorizontalStrut(5));
		add(searchAll);
		add(Box.createHorizontalStrut(5));
		add(searchButton);

		searchButton.addActionListener(this);
		searchTextfield.addActionListener(this);
		searchCategory.addActionListener(this);

		updateAutoCompleteDictionary();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == searchButton) {
			startSearch();
		} else if (e.getSource() == searchTextfield) {
			startSearch();
		} else if (e.getSource() == searchCategory) {
			updateAutoCompleteDictionary();
		}
	}

	private void updateAutoCompleteDictionary() {
		String name = (String) searchCategory.getSelectedItem();
		Collection<String> values = Dictionary.getValues(name);
		logger.debug(name + ", available dictionary size=" + values.size());
		autoCompleteDocument.setDictionary(values);
	}

	private void startSearch() {
		String pattern = searchTextfield.getText().trim();
		String category = (String) searchCategory.getSelectedItem();
		LinkedList<ResourceAndViewNode> rscs = new LinkedList<ResourceAndViewNode>();
		if (pattern.trim().length() == 0 || category == null) {

		} else {
			// for speed of remote access and consideration that rsc remember
			// its center
			// it is better search from Containers not from Dao.
			// ResourceDao rdao = DaoFactory.getDaoFactory().getResourceDao();
			// String center = searchAll.isSelected() ? null :
			// MainFrame.getContent().getSelectedResourceContainer().getName();
			// Object rscs = center == null ?
			// rdao.getResources(pattern, category) :
			// rdao.getResources(pattern, category, center);
			pattern = Util.toJavaStyle(pattern);
			Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
			Iterator<ResourceAndViewNode> i = displayer.getResources(searchAll.isSelected());
			while (i.hasNext()) {
				ResourceAndViewNode rn = i.next();
				String value = rn.getRsc().getProperty(category);
				if (value == null || value.length() == 0)
					continue;
				if (p.matcher(value).find() || p.matcher(value).matches()) {
					rscs.add(rn);
				}
			}
			displayer.displayResources(rscs);
		}
		MainFrame.getContent().setStatus(rscs.size() + " items found");

	}
}
