package edu.ucla.loni.ccb.itools.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

public class IatrPane extends JPanel implements IResourceContainer {
	IndexOrganizer indexOrganizer;
	IatrResourceIndexTree iatrTree;

	public IatrPane(IatrResourceIndexTree iatrTree) {
		super(new BorderLayout());
		setName("IATR");
		this.iatrTree = iatrTree;
		iatrTree.setPane(this);
		indexOrganizer = new IndexOrganizer(iatrTree);
		add(new JScrollPane(iatrTree), BorderLayout.CENTER);
		add(indexOrganizer, BorderLayout.SOUTH);

		indexOrganizer.setBorder(new TitledBorder("Ontology:"));
	}

	public Collection getResources() {
		return iatrTree.getResources();
	}

	// public void save() {
	// iatrTree.save();
	// }

	public void load() {
		iatrTree.load();
	}

	public void treeLoaded() {
		indexOrganizer.treeLoaded();
	}

	public String toString() {
		return getName();
	}

	public String getUrlString() {
		return iatrTree.getUrlString();
	}

	public void setUrlString(String urlstring) {
		iatrTree.setUrlString(urlstring);
	}

	class IndexOrganizer extends JPanel implements ActionListener {
		private JComboBox allfieldNames = new JComboBox(Descriptor
				.getAllFieldNames());
		private JTextField ontology = new JTextField(Descriptor.BYLINE + "/"
				+ Descriptor.PROP_NAME);
		private JButton goButton = new JButton("Go");
		private IatrResourceIndexTree iatrIndexTree;

		public IndexOrganizer(IatrResourceIndexTree tree) {
			iatrIndexTree = tree;
			setupGui();
		}

		public void treeLoaded() {
			DefaultComboBoxModel m = new DefaultComboBoxModel(Descriptor
					.getAllFieldNames());
			allfieldNames.setModel(m);
		}

		private void setupGui() {
			setLayout(new BorderLayout());
			JPanel pane = new JPanel(new GridLayout(2, 1));
			pane.add(allfieldNames);
			pane.add(ontology);
			add(pane, BorderLayout.CENTER);
			add(goButton, BorderLayout.SOUTH);
			goButton.addActionListener(this);
			ontology.addActionListener(this);
			allfieldNames.addActionListener(this);
			iatrIndexTree.ontologyChanged(ontology.getText().trim());
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == allfieldNames) {
				String s = (String) allfieldNames.getSelectedItem();
				String t = ontology.getText().trim();
				if (t.indexOf(s) == -1) {
					if (t.length() != 0 && !t.endsWith("/")) {
						t += "/";
					}
					t += s;
				}
				ontology.setText(t);
			} else {
				iatrIndexTree.ontologyChanged(ontology.getText().trim());
			}
		}
	}

	public void addResource(NcbcResource rsc) {
		iatrTree.addResource(rsc);
	}

	// public void removeResource(NcbcResource rsc) {
	// iatrTree.removeResource(rsc);
	// }

}
