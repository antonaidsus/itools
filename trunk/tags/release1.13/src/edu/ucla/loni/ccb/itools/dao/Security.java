package edu.ucla.loni.ccb.itools.dao;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.model.RUser;
import edu.ucla.loni.ccb.itools.view.GuiUtil;
import edu.ucla.loni.ccb.itools.view.LoginDialog;
import edu.ucla.loni.ccb.itools.view.MainFrame;

/**
 * When run client/server, different accessibility will be set depend on user's role.
 * This class is used to determine user's accessibility.
 * 
 * @author Jeff Qunfei Ma
 *
 */
public class Security {
	private static LoginDialog loginDialog;
	private static String currentRole = "";
	//private static int securityLevel = -1;
	
	private static void showLoginDialog() {
		if (loginDialog == null) {
			loginDialog = new LoginDialog(MainFrame.getContent().getWindowAncestor());
		}
		loginDialog.setVisible(true);	
	}	

    /**
     * @param role either RUser.EXPERTUSER or RUser.NORMALUSER
     * @return
     */
    public static boolean hasPermission(String role) {
    	if (!Main.isAppletMode() && Main.getServerUrl() == null) {
    		return true;
    	}

    	if (currentRoleGood(role)) {
    		return true;
    	}
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			//try to solve some dialog hang problem.
		}
    	//either a  current role is normal user dont have EXPERT or not login yet.
		showLoginDialog();
		
		if (loginDialog.ask()) {
		    currentRole = loginDialog.getUserRole();
		    if (currentRole.equals(RUser.NOTTHING)) {
	    		GuiUtil.showMsg("Failed to Login server.");
	    		return false;
	    	} else {
	    		if (currentRoleGood(role)) {
	    			return true;
	    		} else {
	    			GuiUtil.showMsg("You don't have permission to do this action.");
	    			return false;
	    		}
	    	}
	    } else {
	    	return false;
	    }   	 	
    }
    
    public static boolean currentRoleGood(String role) {
    	if (RUser.ADMINISTRATOR.equals(currentRole)) { //always have permssion.
    		return true;
    	}
    	if (RUser.EXPERTUSER.equals(currentRole)) { 
    		return !RUser.ADMINISTRATOR.equals(role);
    	}
    	
    	if (currentRole.equals(role)) {
    		return true;
    	} 
    	return false;
    }
}
