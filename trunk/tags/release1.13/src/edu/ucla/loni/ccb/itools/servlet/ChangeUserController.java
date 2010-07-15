package edu.ucla.loni.ccb.itools.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.RUser;

public class ChangeUserController extends AbstractCommandController {
	/**
	 * Now it only change the user's role. later on more user changes may
	 * required, so need keep eye on it.
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractCommandController#handle(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object,
	 *      org.springframework.validation.BindException)
	 */
	protected ModelAndView handle(HttpServletRequest req,
			HttpServletResponse res, Object command, BindException errors)
			throws Exception {
		String msg ="";
		if (req.getParameter("new").equals("yes")) {
			msg = createUser(req, res, command, errors);
		} else {
			msg = modifyUser(req, res, command, errors);
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("message", msg);
		//there should be a simpler way, I just don't know
		String currentUser =(String) req.getSession().getAttribute(AccountController.USER_KEY);
		String address = "myAccount.htm";
		if (currentUser != null && currentUser.equals("admin")) {
			address = "admin.htm";
		}

		map.put("address", address);
        
		return new ModelAndView("postChangeUser",map );

	}
	
	private String createUser(HttpServletRequest req, HttpServletResponse res, Object command, BindException errors) {
		String message = "'Create user' sucess";
		try {
			RUser nuser = (RUser) command;
			AccountAdminister.getInstance().createAccount(nuser.getName(), nuser.getPasswd(),
					nuser.getEmail(), "ROLE_EXPERTUSER".equals(nuser.getRole()));

		} catch (Throwable e) {
			message = "'Create user' failed due to " + e.getMessage();
		}
		return message;
	}

	private String modifyUser(HttpServletRequest req, HttpServletResponse res,Object command, BindException errors) {
		String msg = "'Modify user' sucess";
		try {
			RUser nuser = (RUser) command;
			RUser user2 = DaoFactory.getDaoFactory().getUserDao().getUser(
					nuser.getName());
			// reset ROle
			if (!user2.isAdministrator()) {
				user2.setRole(nuser.getRole());
			}
			// reset Email if changed
			String nemail = nuser.getEmail().trim();
			if (nemail.length() != 0 && !nemail.equals(user2.getEmail())) {
				user2.setEmail(nemail);
			}
			// reset password if changed.
			String npasswd = nuser.getPasswd().trim();
			if (npasswd.length() != 0) {
				String encoded = AccountAdminister.encode(npasswd);
				if (!encoded.equals(user2.getPasswd())) {
				    user2.setPasswd(encoded);
				}
			}
			DaoFactory.getDaoFactory().getUserDao().updateUser(user2);
		} catch (RuntimeException e) {
			msg = "'Modify user' failed, due to " + e;
		}
		return msg;
	}
}
