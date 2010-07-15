package edu.ucla.loni.ccb.itools.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;

public class DisplayCenterController extends AbstractCommandController {
	protected ModelAndView handle(HttpServletRequest arg0, HttpServletResponse arg1, Object command, BindException errors) throws Exception {
		String centerName = arg0.getParameter("center");
		//NcbcCenter center = DaoFactory.getDaoFactory().getCenterDao().getCenter(centerName);
		//the above line won't work 100%, because the dao use collator to compare the strings.
		List<NcbcCenter> centers = DaoFactory.getDaoFactory().getCenterDao().getCenters();
		for (NcbcCenter center : centers) {
			if (center.getName().equals(centerName)) {
				return new ModelAndView("displayCenter", "center", center);
			}
		}
		return null;
	}
}
