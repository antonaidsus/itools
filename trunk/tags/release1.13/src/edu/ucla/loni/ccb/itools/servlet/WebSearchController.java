package edu.ucla.loni.ccb.itools.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchHistory;
import edu.ucla.loni.ccb.itools.biositemap.WebSearch;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.parser.BioSiteMapSearchHistoryParser;

public class WebSearchController extends IToolsController {
	public static final Logger LOGGER = Logger
			.getLogger(WebSearchController.class);
	private WebSearch webSearch;

	public ModelAndView handleRequest(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		String v = req.getParameter("method");
		String message = req.getRequestURI();
		LOGGER.debug(message);
		if (v == null) {
			LOGGER.warn("No method parameter, don't know what to do.");
		} else if (v.equals("getBioSiteMapHistory")) {
			message = getBioSiteMapSearchHistory();
		} else if (v.equals("startSearch")) {
			webSearch.setForceUpdate("true".equals(req
					.getParameter("forceupdate")));
			webSearch.startSearch();
			message = " Your search was sucessful, Here is the history\n\n"
					+ getBioSiteMapSearchHistory();
		} else {
			message = " method =" + v + " was not defined.";
		}
		return writeResponse(res, message);
	}

	private String getBioSiteMapSearchHistory() {
		BioSiteMapSearchHistory history = DaoFactory.getDaoFactory()
				.getWebSearchHistoryDao().getHistory();
		return BioSiteMapSearchHistoryParser.toXML(history);
	}

	public WebSearch getWebSearch() {
		return webSearch;
	}

	public void setWebSearch(WebSearch webSearch) {
		this.webSearch = webSearch;
	}

}
