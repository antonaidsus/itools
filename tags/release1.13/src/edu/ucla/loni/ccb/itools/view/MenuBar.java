/*
COPYRIGHT NOTICE
Copyright (c) 2005  Jeff Q. Ma and Arthur W. Toga

See README.license for license notices.
*/

/*
 * created on Apr 22, 2005, $ID$
 */
package edu.ucla.loni.ccb.itools.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.action.AboutAction;
import edu.ucla.loni.ccb.itools.action.ExitAction;

/**
 * This is the viewer's JMenubar.
 * Eventually will be deprecated.
 * 
 * @author <A HREF="mailto:qma@loni.ucla.edu">Jeff Ma </A>
 * @version Apr 22, 2005
 */
public class MenuBar extends JMenuBar implements ActionListener {
    /** Text for menu "File". */
    public static final String FILE = "File";
    public static final String HELP = "Help";
    /** Text for menuItem "About". */
    public static final String ABOUT = "About";

    /** Text for menuItem "Help Contents". */
    public static final String HELP_CONTENTS = "Help Contents";
    
    /** The file menu. */
    protected JMenu fileMenu = new JMenu(MenuBar.FILE);
    /** The help menu. */
    protected JMenu helpMenu = new JMenu(MenuBar.HELP); 
    
    protected JMenu viewMenu = new JMenu("View");
    JCheckBoxMenuItem toggleIgnoreCenter = new JCheckBoxMenuItem("Ignore Centers");
    
    JMenuItem iTools_home = new JMenuItem("iTools Home");
    JMenuItem iTools_usage = new JMenuItem("iTools Usage");


    /**
     * Creates JMenuBar and adds it's menuItems.
     * 
     * @param viewer the owner Viewer of this MenuBar.
     */
    public MenuBar() {
        if (!Main.isAppletMode()) {
        	add(fileMenu);
        	fileMenu.add(new ExitAction("Exit"));
        }
        
//        add(viewMenu);
//        viewMenu.add(toggleIgnoreCenter);
//        toggleIgnoreCenter.addActionListener(this);
        // Help Menu
        add(helpMenu);
        helpMenu.add(new AboutAction(ABOUT));
        helpMenu.add(iTools_home);
        helpMenu.add(iTools_usage);
        
        iTools_home.addActionListener(this);
        iTools_usage.addActionListener(this);
    }


	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if (src == iTools_home) {
			MainFrame.getContent().openUrl(Main.getProperty("itools_home"));
		} else if (src == iTools_usage) {
			MainFrame.getContent().openUrl(Main.getProperty("itools_usage"));
		}		
	}
}
