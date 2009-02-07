package edu.ucla.loni.ccb.itools.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.RUser;

public class AdminController implements Controller {

	public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse rep) throws Exception {
		String currentUser =(String) req.getSession().getAttribute(AccountController.USER_KEY);
		if (currentUser.equals("admin")) {
			String[] users = AccountAdminister.getInstance().getAllUserNames();
			return new ModelAndView("askNameForm", "users", users);
		} else {
			RUser user = DaoFactory.getDaoFactory().getUserDao().getUser(currentUser);
		    return new ModelAndView("displayUser", "user", user);
		}
	}

}
