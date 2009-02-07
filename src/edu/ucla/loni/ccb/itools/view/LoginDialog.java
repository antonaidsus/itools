package edu.ucla.loni.ccb.itools.view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.dao.remote.ProxyServer;
import edu.ucla.loni.ccb.itools.model.RUser;

/**
 * 
 * This dialog let the user login to server.
 * @author Jeff Qunfei Ma
 *
 */
public class LoginDialog extends MyJDialog {
	JTextField usernameField = new JTextField();
	private JPasswordField passwordField = new JPasswordField();
	JButton signupButton = new JButton("Sign up for an account");

	JPanel contentPane = new JPanel(new GridBagLayout());
	
	private String securityRole = RUser.NOTTHING;

	public LoginDialog(Component comp) {
		super(comp);
		setModal(true);
		setupGui();
		pack();
		getBanner().setSubtitle("Login to server or sign up for an account");
		getBanner().setIcon(MainFrame.ITOOLS_ICON);
	}

	private void setupGui() {
		contentPane.setBorder(new EmptyBorder(12, 12, 12, 12));
		int row = 0;
		GuiUtil.addComponent(contentPane, new JLabel("Username"), 0, row, 1, 1);
		GuiUtil.addComponent(contentPane, usernameField, 1, row, 1, 1);

		GuiUtil.addComponent(contentPane, new JLabel("Password"), 0, ++row, 1,
				1);
		GuiUtil.addComponent(contentPane, passwordField, 1, row, 1, 1);

		JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pane.add(signupButton);
		GuiUtil.addComponent(contentPane, signupButton, 1, ++row, 1, 1);
		signupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegisterDialog rd = new RegisterDialog(LoginDialog.this);
				rd.setVisible(true);
			}
		});
		getContentPane().add(contentPane);
	}

	public void ok() {
		if (Main.getServerUrl() == null) { //stand alone application and save to local
			securityRole = RUser.ADMINISTRATOR;
			// local no server
			return;
		}
		//to use Acegi security, we don't encode it now. but use https.
//		String urlStr = "account.htm?method=login"
//				+ "&j_username=" + usernameField.getText().trim()
//				+ "&j_password=" + new String(passwordField.getPassword());
		String urlStr = "account.htm";
		try {
			Properties properties = new Properties();
			properties.put("method", "login");
			properties.put("j_username", usernameField.getText().trim());
			properties.put("j_password", new String(passwordField.getPassword()));
			securityRole = ProxyServer.talk2HttpsServer(urlStr, properties);
		} catch (Exception e) {
			Main.LOGGER.warn(e, e);
		}
		Main.putProperty("author", usernameField.getText().trim());
		usernameField.setText("");
		passwordField.setText("");

		super.ok();
	}

	public void cancel() {
		securityRole =RUser.NOTTHING;
		usernameField.setText("");
		passwordField.setText("");
		super.cancel();
	}
	
	public String getUserRole() {
		return securityRole;
	}
}
