package edu.ucla.loni.ccb.itools.servlet;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;

public class ChangeCenterController extends AbstractCommandController {
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
		NcbcCenter center = (NcbcCenter) command;
		String msg = null;
		if (req.getParameter("delete") != null) {
			DaoFactory.getDaoFactory().getCenterDao().deleteCenter(center.getName());
			msg = "'Delete center' succeed.";
		} else if (req.getParameter("update") != null){
			DaoFactory.getDaoFactory().getCenterDao().updateCenter(center);
			msg = "'Update center' succeed.";
		} else if (req.getParameter("updateFromUrl") != null) {
			msg = "'Update center data from Url' " + ResourceController.doUpdateFromUrl(center.getName(), center.getUrl());
		} else {
			msg ="Don't know what to do.";
		}
	
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("message", msg);
		//there should be a simpler way, I just don't know
		String currentUser =(String) req.getSession().getAttribute(AccountController.USER_KEY);
		String address = "myAccount.htm";
		if (currentUser != null && currentUser.equals("admin")) {
			address = "admin.htm";
		}

		map.put("address", address);

		return new ModelAndView("postChangeUser", map);

	}	

}
