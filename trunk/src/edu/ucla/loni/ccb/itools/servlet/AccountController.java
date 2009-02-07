package edu.ucla.loni.ccb.itools.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

/**
 * This spring controller deal with the acount issues with a client request.
 * Before user can access restrict resources, such as modify a resource, the
 * user must login to server.
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class AccountController extends IToolsController {
	public static final Logger LOGGER = Logger
			.getLogger(AccountController.class);

	public final static String USER_KEY = AuthenticationProcessingFilter.ACEGI_SECURITY_LAST_USERNAME_KEY;

	AuthenticationProcessingFilter authProcessor;

	public ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		String v = req.getParameter("method");
		String message = req.getRequestURI();
		if (v == null) {
			LOGGER.warn("secure: No method parameter, don't know what to do.");
		} else if (v.equals("register")) {
			String name = req.getParameter("user");
			String password = req.getParameter("password");
			String email = req.getParameter("email");
			String role = req.getParameter("role");
			try {
				AccountAdminister.getInstance().createAccount(name, password,
						email, "expert".equals(role));
				message = "sucess";
			} catch (Throwable e) {
				LOGGER.warn(e);
				message = e.getMessage();
			}
		} else if (v.equals("login")) {
			// String name = new
			// String(Base64.decrypt(req.getParameter("user")));
			// byte[] password = Base64.decrypt(req.getParameter("password"));
			// RUser ruser = AccountAdminister.getInstance().validateUser(name,
			// password);
			// if (!ruser.getRole().equals(RUser.NOTTHING)) {
			// save2Session(ruser,req.getSession(true));
			// }
			// String role = ruser.getRole();
			// LOGGER.info(name + ":" + role);
			// message = role;
			try {
				Authentication authResult = authProcessor
						.attemptAuthentication(req);
				SecurityContextHolder.getContext()
						.setAuthentication(authResult);
				LOGGER.debug("Principal:" +authResult.getPrincipal().getClass().getName());
				message = authResult.getAuthorities()[0].getAuthority();
			} catch (Throwable e) {
				LOGGER.warn(e);
				message = e.getMessage();
			}
		}
		return writeResponse(res, message);
	}

	public AuthenticationProcessingFilter getAuthProcessor() {
		return authProcessor;
	}

	public void setAuthProcessor(AuthenticationProcessingFilter authProcessor) {
		this.authProcessor = authProcessor;
	}
}
