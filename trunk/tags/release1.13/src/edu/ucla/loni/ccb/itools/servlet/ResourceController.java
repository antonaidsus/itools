package edu.ucla.loni.ccb.itools.servlet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.CSVParser;
import edu.ucla.loni.ccb.itools.parser.CentersParser;
import edu.ucla.loni.ccb.itools.parser.NcbcResourceParser;
import edu.ucla.loni.ccb.itools.parser.RdfParser;
import edu.ucla.loni.ccb.itools.parser.XlsParser;

/**
 * This controller handles requests for NcbcResources which don't need login
 * first.
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class ResourceController extends IToolsController {
	public static final Logger logger = Logger
			.getLogger(ResourceController.class);
	public static final String RECOVER_FROM_LAST_UPDATE = "Recover from last update";

	public ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		String v = req.getParameter("method");
		String message = req.getRequestURI() + ", method=" + v;
		String center = req.getParameter("center");
		if (center != null) {
			message += "center=" + center;
		}
		logger.debug(message);
		if (v == null) {
			logger.warn("No method parameter, don't know what to do.");
		} else if (v.equals("getCenters")) {
			 List<NcbcCenter> centers = DaoFactory.getDaoFactory().getCenterDao()
					.getCenters();
			message = CentersParser.toXml(centers);
		} else if (v.equals("getResources")) {
			String reg = req.getParameter("reg");

			String category = req.getParameter("category");

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
			List<NcbcResource> resources = DaoFactory.getDaoFactory().getResourceDao()
					.load(center);
			message = (NcbcResourceParser.getSaveString(resources));
		} else if (v.equals("getOntology")) {
			message = DaoFactory.getDaoFactory().getResourceDao().getOntologyString();
		} else if (v.equals("updateFromUrl")) {
			//if success the message should be startsWith "good"
			//see ResourceIndexTree.updateFromInternet()
			String urlStr = req.getParameter("urlStr");
			message = doUpdateFromUrl(center, urlStr);
		} else if (v.equals("exportAsXls")) {
			List<NcbcResource> rscs = DaoFactory.getDaoFactory().getResourceDao()
			.load(center);
			
			res.setContentType("application/octet-stream");
			res.setContentType("application/vnd.ms-excel");
			ServletOutputStream outputStream = res.getOutputStream();
			XlsParser.resource2Xls(rscs, outputStream);
			outputStream.close();
            return null;
		}
		
		try {
			writeResponse(res, message);
		} catch (Exception e) {
			logger.info( e.getMessage() + " when writing to response error:"/* + Util.wrapString(message)*/);
		}
		return null;
	}

	/**
	 * @param req
	 * @return True indicating success.
	 */
	public static String doUpdateFromUrl(String centerName, String urlStr) {
		String message = "good"; //value indicating succeed. used at ResourceIndexTree also
		try {
			List<NcbcResource> resources = null;
			logger.info("updateFromUrl:" + urlStr);
			if (urlStr.startsWith(RECOVER_FROM_LAST_UPDATE)) {
				DaoFactory.getDaoFactory().getResourceDao()
						.recover(centerName);
			} else {
				InputStream openStream = new URL(urlStr).openStream();
				if (urlStr.endsWith(".xml")) {
					resources = NcbcResourceParser.parse(openStream);
				} else if (urlStr.endsWith(".csv")) {
					resources = CSVParser.parse(openStream);
				} else if (urlStr.endsWith(".rdf")) {
					RdfParser rdfParser = new RdfParser();
					rdfParser.setForSandBox(false);
					resources = rdfParser.parse(openStream);
				} else if (urlStr.endsWith(".xls")) {
					XlsParser parser = new XlsParser();
					parser.setForSandBox(false);
					resources = parser.parse(openStream);
				} else {
					logger.warn("don't know how to parse the format.");
				}
                
				if (resources != null && resources.size() > 0) {
					List<NcbcResource> old = DaoFactory.getDaoFactory().getResourceDao().load(centerName);
					message += ", previously resources size: " + old.size() + ", new resources size: " + resources.size();
					logger.info(resources.size() + " resources was set.");
					DaoFactory.getDaoFactory().getResourceDao().setResources(
							resources);
				} else {
					message = "false";
				}
			}
		} catch (Exception e) {
			message = "error: " + e.getMessage();
			logger.warn(message, e);
		}
        return message;
	}

	static NcbcResource string2Resource(HttpServletRequest req) {
		String center = req.getParameter("center");
		String rscstr = req.getParameter("rsc");
		logger.debug(rscstr);
		List<NcbcResource> list = NcbcResourceParser.parse(new ByteArrayInputStream(rscstr
				.getBytes()));
		if (list.size() == 0) {
			logger.error("no resource created from:\n" + rscstr);
			return null;
		}
		NcbcResource resource = list.get(0);
		resource.setCenter(center);
		return resource;
	}

}
