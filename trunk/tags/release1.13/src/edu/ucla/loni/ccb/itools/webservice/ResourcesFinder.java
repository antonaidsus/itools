package edu.ucla.loni.ccb.itools.webservice;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.dao.ResourceDao;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.BioSiteMapParser;

/**
 * 
 * This class implements the webservice. It currently does not dynamic load
 * available resources.
 * 
 * @author Jeff Ma
 * 
 */
public class ResourcesFinder {
	public ResourcesFinder() {
		Main.setServerMode(true);
		Main.loadProperties(true);
	}

	public String findResources(String reg, String category) {
		ResourceDao rdao = DaoFactory.getDaoFactory().getResourceDao();
		List<NcbcResource> resources = rdao.getResources(reg, category);
		if (resources.size() == 0) {
			return "Your search \"" + reg + "\" from \"" + category
					+ "\" did not match any resources";
		}

		StringWriter sw = new StringWriter();
		sw.write(resources.size() + " resources found\n");

		// sw.write(NcbcResourceParser.getSaveString(resources));
		sw.write(BioSiteMapParser.getSaveString(resources));
		try {
			sw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sw.toString();
	}

}
