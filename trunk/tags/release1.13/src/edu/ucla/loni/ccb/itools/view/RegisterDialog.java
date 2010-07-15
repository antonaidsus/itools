package edu.ucla.loni.ccb.itools.view;

import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.Messages;
import edu.ucla.loni.ccb.itools.dao.remote.ProxyServer;


/**
 * This dialog let user create an account.
 * @author Jeff Qunfei Ma
 *
 */
public class RegisterDialog extends MyJDialog {
    private JTextField userid = new JTextField(25);
    JPasswordField passwd1 = new JPasswordField(25);
    JPasswordField passwd2 = new JPasswordField(25);
    JTextField email = new JTextField(25);
    JCheckBox expertRole = new JCheckBox("Expert user?");

	public RegisterDialog(JDialog dialog) {
		super(dialog,"Creating an Acount", true);
		setupGui();
		pack();
	}

	private void setupGui() {
		getBanner().setSubtitle(Messages.getString("server.account.expertuser"));
		JPanel pane = new JPanel(new GridBagLayout());
		int r = 0;
        GuiUtil.addComponent(pane, new JLabel("User Name"), 0, r, 1, 1);
        GuiUtil.addComponent(pane, userid, 1, r++, 1, 1);

        GuiUtil.addComponent(pane, new JLabel("Password"), 0, r, 1, 1);
        GuiUtil.addComponent(pane, passwd1, 1, r++, 1, 1);

        GuiUtil.addComponent(pane, new JLabel("Confirm Password"), 0, r, 1, 1);
        GuiUtil.addComponent(pane, passwd2, 1, r++, 1, 1);
        
        GuiUtil.addComponent(pane, new JLabel("E-mail"), 0, r, 1, 1);
        GuiUtil.addComponent(pane, email, 1, r++, 1, 1);
        
        GuiUtil.addComponent(pane, expertRole, 1, r++, 1, 1);
        getContentPane().add(pane);
	}
	
	public void ok() {
		String pw1 = new String(passwd1.getPassword());
		String pw2 = new String(passwd2.getPassword());
		
		if (!pw1.equals(pw2)) {
			passwd1.setText("");
		    passwd2.setText("");
		    GuiUtil.showMsg(Messages.getString("server.account.error1"));
		    return;
	    }
		
		
		if (Main.getServerUrl() == null) {
			//local no server, is should never go here.
			return;
		}
//		String urlStr = "account.htm?method=register"
//		    + (expertRole.isSelected() ? "&role=" + Base64.encrypt("expert".getBytes()) : "")
//		    + "&user=" + Base64.encrypt(userid.getText().trim().getBytes()) 
//		    + "&password=" + Base64.encrypt(pw1.getBytes())
//		    + "&email=" + Base64.encrypt(email.getText().getBytes());

		String urlStr = "account.htm?method=register"
		    + (expertRole.isSelected() ? "&role=expert" : "")
		    + "&user=" + userid.getText().trim() 
		    + "&password=" + pw1
		    + "&email=" + email.getText();
		try {
			String line = ProxyServer.talk2HttpsServer(urlStr);
			Main.LOGGER.debug("registdialog:"+ line);
			if (line.indexOf("sucess") != -1) {
				line = Messages.getString("server.account.created");
				super.ok();
			}
			GuiUtil.showMsg(line);
		} catch (Exception e) {
			Main.LOGGER.warn(e,e);
		}
	}	
}
