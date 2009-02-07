package edu.ucla.loni.ccb.itools.servlet;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.model.RUser;
import edu.ucla.loni.ccb.itools.parser.NcbcResourceParser;

/**
 * 
 * The services this class provided needs authentication first. The
 * ROLE_EXPERTUSER or higher is required. Due to security issue, this controller
 * should talk to client in SSL.
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class RestrictedResourceController extends ResourceController {
	public ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		String v = req.getParameter("method");
		String message = req.getRequestURI();
		String currentUser = (String) req.getSession().getAttribute(
				AccountController.USER_KEY);
		LOGGER.debug("Restricted:" + message + ",method=" + v + ", username:"
				+ currentUser);
		RUser user = DaoFactory.getDaoFactory().getUserDao().getUser(
				currentUser);

		if (v == null) {
			LOGGER.warn("secure: No method parameter, don't know what to do.");
		} else if (v.equals("add")) {
			NcbcResource rsc = string2Resource(req);
			if (rsc != null) {
				if (!rsc.isInSandBox() && user.isNormalUser()) {
					message += "add failed, not permitted";
				} else {
					DaoFactory.getDaoFactory().getResourceDao()
							.addResource(rsc);
					message += "add, sucessed";
				}
			}

		} else if (v.equals("update")) {
			NcbcResource rsc = string2Resource(req);
			if (rsc != null) {
				if (!rsc.isInSandBox() && user.isNormalUser()) {
					message += "update failed, not permitted";
				} else {
					DaoFactory.getDaoFactory().getResourceDao().updateResource(
							rsc);
					message += "update sucessed";
				}
			}
		} else if (v.equals("remove")) {
			NcbcResource rsc = string2Resource(req);
			if (rsc != null) {
				if (!rsc.isInSandBox() && user.isNormalUser()) {
					message += "remove failed, not permitted";
				} else {
					DaoFactory.getDaoFactory().getResourceDao().removeResource(
							rsc);
					message += "remove sucessed";
				}
			}
		} else if (v.equals("setResources")) {
			if (user.isNormalUser()) {
				message += "setResources failed, not permitted";
			} else {
				String str = req.getParameter("rscs");
				List<NcbcResource> list = NcbcResourceParser.parse(new ByteArrayInputStream(
						str.getBytes()));
				String center = ((NcbcResource) list.get(0)).getCenter();
				DaoFactory.getDaoFactory().getResourceDao().setResources(list);
				LOGGER.debug(center
						+ ",center updated sucessful, mail the administrator");
				RUser ruser = DaoFactory.getDaoFactory().getUserDao().getUser(
						currentUser);
				try {
					AccountAdminister.getInstance().getMailService().sendMail(
							center, ruser);
				} catch (Exception e) {
					LOGGER.warn(e);
				}
			}
		} else if (v.equals("recover")) {
			if (user.isNormalUser()) {
				message += "recover failed, not permitted";
			} else {
				String center = req.getParameter("center");
				List<NcbcResource> rv = DaoFactory.getDaoFactory().getResourceDao().recover(center);
				message = NcbcResourceParser.getSaveString(rv);
				LOGGER.debug(center
						+ ",center recover sucessful, mail the administrator");
				RUser ruser = DaoFactory.getDaoFactory().getUserDao().getUser(
						currentUser);
				try {
					AccountAdminister.getInstance().getMailService().sendMail(
							center, ruser);
				} catch (Exception e) {
					LOGGER.warn(e);
				}
			}
		} else if (v.equals("canRecover")) {
				String center = req.getParameter("center");
				message = DaoFactory.getDaoFactory().getResourceDao().canRecover(center) +"";	
				LOGGER.debug("can Recover:" + message);
		} else {
			return super.handleRequest(req, res);
		}
		return writeResponse(res, message);
	}
}
