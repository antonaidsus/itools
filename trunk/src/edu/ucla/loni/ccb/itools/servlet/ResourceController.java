package edu.ucla.loni.ccb.itools.servlet;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.CentersParser;
import edu.ucla.loni.ccb.itools.parser.NcbcResourceParser;

/**
 * This controller handles requests for NcbcResources which don't need login
 * first.
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class ResourceController extends IToolsController {
	public static final Logger LOGGER = Logger
			.getLogger(ResourceController.class);

	public ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		String v = req.getParameter("method");
		String message = req.getRequestURI();
		// LOGGER.debug(message);
		if (v == null) {
			LOGGER.warn("No method parameter, don't know what to do.");
		} else if (v.equals("getCenters")) {
			 List<NcbcCenter> centers = DaoFactory.getDaoFactory().getCenterDao()
					.getCenters();
			message = CentersParser.toXml(centers);
			LOGGER.debug(message);
		} else if (v.equals("getResources")) {
			String reg = req.getParameter("reg");

			String category = req.getParameter("category");

			String center = req.getParameter("center");
			List<NcbcResource> resources = null;
			if (center == null) {
				resources = DaoFactory.getDaoFactory().getResourceDao()
						.getResources(reg, category);
			} else {
				resources = DaoFactory.getDaoFactory().getResourceDao()
						.getResources(reg, category, center);
			}
			message = NcbcResourceParser.getSaveString(resources);
		} else if (v.equals("load")) {
			String center = req.getParameter("center");
			List<NcbcResource> resources = DaoFactory.getDaoFactory().getResourceDao()
					.load(center);
			message = (NcbcResourceParser.getSaveString(resources));
		}
		return writeResponse(res, message);
	}

	// static NcbcResource string2Resource(HttpServletRequest req) {
	// // LOGGER.warn("query=" + req.getQueryString());
	// // LOGGER.warn("rsc=" + req.getParameter("rsc"));
	// String center = req.getParameter("center");
	// String rsc = req.getParameter("rsc");
	// rsc = rsc.replaceAll(" ", "+");
	//
	// String rscstr = new String(Base64.decode(rsc));
	// //rscstr = StringEscapeUtils.unescapeXml(rscstr);
	// List list = NcbcResourceParser.parse(new
	// ByteArrayInputStream(rscstr.getBytes()));
	// if (list.size() == 0) {
	// LOGGER.error("no resource created from:\n" + rscstr);
	// return null;
	// }
	// NcbcResource resource = (NcbcResource) list.get(0);
	// resource.setCenter(center);
	// return resource;
	// }

	static NcbcResource string2Resource(HttpServletRequest req) {
		String center = req.getParameter("center");
		String rscstr = req.getParameter("rsc");
		LOGGER.debug(rscstr);
		List<NcbcResource> list = NcbcResourceParser.parse(new ByteArrayInputStream(rscstr
				.getBytes()));
		if (list.size() == 0) {
			LOGGER.error("no resource created from:\n" + rscstr);
			return null;
		}
		NcbcResource resource = list.get(0);
		resource.setCenter(center);
		return resource;
	}

}
