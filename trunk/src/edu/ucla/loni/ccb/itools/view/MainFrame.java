package edu.ucla.loni.ccb.itools.view;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.Messages;

public class MainFrame extends JFrame {
	private static MainFrame frame;

	private static ContentPane contentPane;
	
	public static final ImageIcon ITOOLS_ICON = GuiUtil.getIcon(Messages
			.getString("Main.0")); //$NON-NLS-1$

	public static final ImageIcon ITOOLS_WATERMARK = GuiUtil.getIcon(Messages
			.getString("Main.1")); //$NON-NLS-1$


	public MainFrame() {
		setTitle("NCBC iTools");
		setIconImage(ITOOLS_ICON.getImage());
		//setJMenuBar(new MenuBar());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });//
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(1000, d.width * 4 / 5), Math.min(850, d.height * 4 / 5));
        setLocation( (d.width - getWidth()) / 2, (d.height - getHeight()) / 2);
	}
	
	public static ContentPane getContent() {
		return contentPane;
	}

	public static void setContent(ContentPane pane) {
		contentPane = pane;
	}

	public static void main(String[] args) {
		boolean appletmode = false;
//		if (args.length > 0) {
//			appletmode = args[0].equals("true");			
//		}
//		
		Main.setAppletMode(appletmode);
		Main.loadProperties(false);
		try {
			if (args.length ==2) {
				Main.setServerUrl(args[0]);
				Main.setSecureServerUrl(args[1]);
			} else {
				String serverUrl = Main.getProperty("server.url");
				String property = System.getProperty("server.url");
				if (property != null) {
					serverUrl = property;
					Main.setServerUrl(property);
				} 
				if (serverUrl !=null) {
					String secureUrl = System.getProperty("secure_server.url");
					if (secureUrl == null) {
						secureUrl = Main.getProperty("secure_server.url");
						if (secureUrl == null) {
							int sport = 8443;
							int index0 = serverUrl.indexOf(':');
							int index1 = serverUrl.indexOf(':',index0+1);
							if (index1 == -1) {
								index1 = serverUrl.length();
								sport = 443;
							}
							secureUrl = "https" + serverUrl.substring(index0, index1)+ ":" + sport;
						}
					}
					Main.setSecureServerUrl(secureUrl);
				}
			}
			Main.LOGGER.debug("serverUrl=" + Main.getServerUrl());
			Main.putProperty("author", System.getProperty("user.home"));
		} catch (RuntimeException e) {
			Main.LOGGER.debug(e);
		}

		new ContentPane().init();
		frame = new MainFrame();
		frame.setContentPane(contentPane/* .getContentPane() */);
		frame.setVisible(true);
	}
	
	public static MainFrame getInstance() {
		return frame;
	}
}
