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

public class DisplayUserController extends AbstractCommandController {
	protected ModelAndView handle(HttpServletRequest arg0, HttpServletResponse arg1, Object command, BindException errors) throws Exception {
		RUser user = (RUser)command;
		user = DaoFactory.getDaoFactory().getUserDao().getUser(user.getName());
		String currentUser =(String) arg0.getSession().getAttribute(AccountController.USER_KEY);
		Map map = new HashMap();
		map.put("user", user);
		if (currentUser.equals("admin")) {
			map.put("displayedByadministrator", "true");
		}

		return new ModelAndView("displayUser", map);
	}
}
