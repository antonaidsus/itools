package edu.ucla.loni.ccb.itools;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.BiomedicalRscOntologyParser;
import edu.ucla.loni.ccb.itools.parser.Node;

/**
 * This class behave like cache of the data, especially at Client side. so the client
 * don't have to connect the server every time need the data.
 * 
 * @author qma@ucla.edu
 *
 */
/**
 * @author qma
 *
 */
public class DataBank {
	private static final Logger logger = Logger.getLogger(DataBank.class);
	private static DataCache cache = new DataCache();

	/**
	 * Loads all data from server.
	 */
	public static void loadData() {
		BiomedicalRscOntologyParser parser = new BiomedicalRscOntologyParser();
	    String tmp = DaoFactory.getDaoFactory().getResourceDao().getOntologyString();
		parser.load(new ByteArrayInputStream(tmp.getBytes()));		
        cache.setOntology(parser.getFinalSetOfNodes());
		cache.setDeprecatedOntology(parser.getDeprecatedOntologies());
		
		List<NcbcCenter> centers = DaoFactory.getDaoFactory().getCenterDao().getCenters();
		cache.setCenters(centers);
		logger.debug("number of center=" + centers.size());
		
		Map<String, List<NcbcResource>> loadedResources = new HashMap<String, List<NcbcResource>>();;
		for ( NcbcCenter center : centers) {
			String name = center.getName();
			List<NcbcResource> load = DaoFactory.getDaoFactory().getResourceDao().load(name);
			loadedResources.put(name, load);
		}	
		cache.setLoadedResources(loadedResources);
	}
	
	public static List<NcbcCenter> getCenters() {
		return cache.getCenters();
	}
	
	public static List<NcbcResource> getResources(String centerName) {
		return cache.getLoadedResources().get(centerName);
	}
	
	public static List<NcbcResource> refreshCenter(String centerName) {
		List<NcbcResource> load = DaoFactory.getDaoFactory().getResourceDao().load(centerName);
		cache.getLoadedResources().put(centerName, load);
		return load;
	}
	
	public static List<Node> getOntology() {
		return cache.getOntology();
	}
	
	public static Map<String, ArrayList<String>> getDeprecatedOntology() {
		return cache.getDeprecatedOntology();
	}
	
	public static List<NcbcResource> getAllResources() {
		List<NcbcResource> allLoadedResources = new LinkedList<NcbcResource>();
		for (List<NcbcResource> l : cache.getLoadedResources().values()) {
			allLoadedResources.addAll(l);
		}

		return allLoadedResources;
	}
	
	public static void addResource(NcbcResource rsc) {
		List<NcbcResource> list = deletResourceFromMemory(rsc);
		list.add(rsc);
		DaoFactory.getDaoFactory().getResourceDao().addResource(rsc);
	}
	
	public static void removeResource(NcbcResource rsc) {
		deletResourceFromMemory(rsc);
		DaoFactory.getDaoFactory().getResourceDao().removeResource(rsc);
	}
	
	public static void updateResource(NcbcResource rsc) {
		List<NcbcResource> list = deletResourceFromMemory(rsc);
		list.add(rsc);
		DaoFactory.getDaoFactory().getResourceDao().updateResource(rsc);
	}

	/**
	 * Removes the resources from the bank. the resource can be removed only if 
	 * resource in the bank has the same name as the input Resource and they are in
	 * the same security category (sandbox) or the bank resource is in sandbox.
	 * @param rsc
	 * @return
	 */
	static List<NcbcResource> deletResourceFromMemory(NcbcResource rsc) {
		List<NcbcResource> list = getResources(rsc.getCenter());
		String name = rsc.getName();
		for (Iterator<NcbcResource> iter = list.iterator(); iter.hasNext();) {
			NcbcResource element = iter.next();
			if (element == null) {
				//sounds strange, but it happened sometimes
				logger.warn(rsc.getCenter() + " has null element");
				iter.remove();
			} else if (element.getName().equals(name)) {
				if (element.isInSandBox() == rsc.isInSandBox()
						|| element.isInSandBox()) {
					iter.remove();
					break;
				}
			}
		}
		return list;
	}

}
