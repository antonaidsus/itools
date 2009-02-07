package edu.ucla.loni.ccb.itools.action;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

import edu.ucla.loni.ccb.itools.view.MainFrame;
import edu.ucla.loni.ccb.itools.view.MessagePanel;

public class AboutAction extends MyAction {
	private static final long serialVersionUID = 1L;
	static String VERSION = "";
	static {
		InputStream resourceAsStream = AboutAction.class
				.getResourceAsStream("/build.number");
		Properties prop = new Properties();
		try {
			prop.load(resourceAsStream);
			VERSION = prop.getProperty("version");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	MessagePanel panel = new MessagePanel();

	String about = "NCBC iTools\n\n"
			+ VERSION
			+ "\n\n"
			+ "NCBC iTools is designed and engineered by the NCBC Yellow-Pages \n"
			+ "and Resourceome Working Group (Ivo Dinov, Daniel Rubin, William Lorensen, \n"
			+ "Jonathan Dugan, Peter Lyster, Stott Parker, Karen Skinner, H.V. Jagadish, \n"
			+ "Henry Chueh and Aris Floratos (http://na-mic.org/Wiki/index.php/NCBC_YPR_WG). \n\n"
			+ "This work is funded by the National Institutes of Health through the NIH Roadmap \n"
			+ "for Medical Research. Information on the National Centers for Biomedical Computing \n"
			+ "can be obtained from http://nihroadmap.nih.gov/bioinformatics. \n\n";

	public AboutAction(String name) {
		super(name);
		panel.setText(about, null);
	}

	public void doAction() {
		JOptionPane.showMessageDialog(MainFrame.getContent()
				.getWindowAncestor(), panel, "About NCBC iTools",
				JOptionPane.PLAIN_MESSAGE, MainFrame.ITOOLS_ICON);
	}
}
