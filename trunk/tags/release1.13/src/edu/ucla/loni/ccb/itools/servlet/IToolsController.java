package edu.ucla.loni.ccb.itools.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.model.RUser;

/**
 * 
 * The top class of the Controller for iTools, other controller should extends
 * this class;
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class IToolsController implements Controller {
	public static final Logger LOGGER = Logger
			.getLogger(IToolsController.class);

	public final static String USER_KEY = "ccb.itools.servlet.SecureResourceDaoService";

	static {
		Main.setServerMode(true);
		Main.loadProperties(true);
	}

	// AccountAdminister accountAdminister;
	//
	// public void setAccountAdminister(AccountAdminister accountAdminister) {
	// this.accountAdminister = accountAdminister;
	// }

	public ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		return null;
	}

	public ModelAndView writeResponse(HttpServletResponse res, String message)
			throws Exception {
		res.setContentType("text/plain");
		ServletOutputStream outputStream = res.getOutputStream();
		outputStream.print(message);
		outputStream.close();
		return null;
	}

	public boolean isAdmin() {
		return hasRole(new String[] { RUser.ADMINISTRATOR });
	}

	public boolean isExpertUser() {
		return hasRole(new String[] { RUser.EXPERTUSER, RUser.ADMINISTRATOR });
	}

	public boolean isNormalUser() {
		return hasRole(new String[] { RUser.NORMALUSER, RUser.EXPERTUSER,
				RUser.ADMINISTRATOR });
	}

	// sometimes it works sometime not.
	public boolean hasRole(String[] roles) {
		Authentication au = SecurityContextHolder.getContext()
				.getAuthentication();
		if (au == null) {
			LOGGER.info("Interesting,authentication=" + au);
			return false;
		}
		GrantedAuthority[] authorities = au.getAuthorities();
		for (int j = 0; j < roles.length; j++) {
			for (int i = 0; i < authorities.length; i++) {
				if (roles[j].equals(authorities[i].getAuthority())) {
					return true;
				}
			}
		}
		return false;
	}

}
