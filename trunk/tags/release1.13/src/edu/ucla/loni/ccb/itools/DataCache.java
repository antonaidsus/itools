package edu.ucla.loni.ccb.itools;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.Node;

public class DataCache {
	private static final Logger logger = Logger.getLogger(DataCache.class);
	// used for compare center.
	private static Collator usCollator = Collator.getInstance(Locale.US);

	List<NcbcCenter> centers = Collections.emptyList();
	Map<String, List<NcbcResource>> loadedResources = new HashMap<String, List<NcbcResource>>();

	List<Node> ontology;
	Map<String, ArrayList<String>> deprecatedOntology;

	static {
		usCollator.setStrength(Collator.PRIMARY);
	}

	public NcbcCenter getCenter(String name) {
		for (NcbcCenter center : centers) {
			if (centerMatchByName(center, name)) {
				return center;
			}
		}

		logger.info("No center defined for '" + name + "'");
		return null;
	}

	public List<NcbcCenter> getCenters() {
		return centers;
	}

	public void setCenters(List<NcbcCenter> centers) {
		this.centers = centers;
	}

	public Map<String, List<NcbcResource>> getLoadedResources() {
		return loadedResources;
	}

	public void setLoadedResources(
			Map<String, List<NcbcResource>> loadedResources) {
		this.loadedResources = loadedResources;
	}

	public static boolean centerMatchByName(NcbcCenter center, String name) {
		return usCollator.equals(center.getName(), name)
				|| usCollator.equals(center.getName() + "Center", name)
				|| (center.getFullName() != null)
				&& usCollator.equals(center.getFullName(), name);
	}

	public List<Node> getOntology() {
		return ontology;
	}

	public void setOntology(List<Node> ontology) {
		this.ontology = ontology;
	}

	public Map<String, ArrayList<String>> getDeprecatedOntology() {
		return deprecatedOntology;
	}

	public void setDeprecatedOntology(
			Map<String, ArrayList<String>> deprecatedOntology) {
		this.deprecatedOntology = deprecatedOntology;
	}
}
