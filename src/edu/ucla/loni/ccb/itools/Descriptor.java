package edu.ucla.loni.ccb.itools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * This class represents one Descriptor (Property, Slot) of Resource. The GUI
 * will use a different Component (JTextField, JTextArea, JComboBox to display
 * it depending on its value.
 * 
 * @author Qunfei Ma
 * 
 */
public class Descriptor {
	// the required field items
	public final static String PROP_ONTOLOGY = "NCBC Ontology Classification";
	public final static String PROP_NAME = "Name";
	public final static String PROP_DESCRIPTION = "Description";
	public final static String PROP_INPUTDATA = "Data Input";
	public final static String PROP_OUTPUTDATA = "Data Output";
	public final static String PROP_LANGUAGE = "Implementation Language";
	public final static String PROP_PLATFORM = "Platforms tested";
	public final static String PROP_ORGANIZATION = "Organization";
	public final static String PROP_KEYWORDS = "Keywords";
	public final static String PROP_LICENSE = "License";
	public final static String PROP_URL = "URL";
	public final static String PROP_AUTHORS = "Authors";
	public final static String PROP_TYPE = "Resource Type";
	public final static String PROP_VDS = "Version, Date, Stage";

	// the following fields are available in IATR CSV format.
	public final static String BYLINE = "Byline";
	public final static String OPENSOURCE = "OpenSource";
	public static final String EXTERNAL_CENTER = "External Initiatives";

	public static final String NOVALUE = "NO_ONTOLOGY";
	public static final String DELIMITER = "/";
	public static final String SEPARATOR = ";";
	public static final String NON_STANDARD = "NON_STANDARD";

	private static String[] requiredNames = { PROP_NAME, PROP_DESCRIPTION,
			PROP_ONTOLOGY, PROP_INPUTDATA, PROP_OUTPUTDATA, PROP_LANGUAGE,
			PROP_PLATFORM, PROP_ORGANIZATION, PROP_KEYWORDS, PROP_LICENSE,
			PROP_URL, PROP_AUTHORS, PROP_VDS };

	// single line will use a JTextField not JTextArea to display
	private boolean singleline = true;
	private boolean required = true;
	private String name;

	/**
	 * Keeps defined Descriptors (des.name, des);
	 */
	private static Map<String, Descriptor> descriptors = new LinkedHashMap<String, Descriptor>();

	/**
	 * keeps all names of the Descriptor.
	 */
	private static List<String> ALLKEYS = new LinkedList<String>();
	/* part of ALLKEYS which is the name of hided Columns */
	private static List<String> unshownKeys = new ArrayList<String>();

	static {
		for (int i = 0; i < requiredNames.length; i++) {
			Descriptor des = requiredNames[i].equals(PROP_DESCRIPTION) ? new Descriptor(
					requiredNames[i], true, false)
					: new Descriptor(requiredNames[i]);
			descriptors.put(requiredNames[i], des);
			addFieldName(requiredNames[i]);
		}
	}

	public Descriptor(String name) {
		this(name, true, true);
	}

	public Descriptor(String name, boolean required, boolean singleLine) {
		this.name = name;
		this.required = required;
		this.singleline = singleLine;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the single line
	 */
	public boolean isSingleline() {
		return singleline;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Gets a Array of String which is all properties a Resource can have. Used
	 * to create all column of the table.
	 * 
	 * @return
	 */
	public static String[] getAllFieldNames() {
		return ALLKEYS.toArray(new String[0]);
	}

	public static List<String> getUnshownKeys() {
		return unshownKeys;
	}

	/**
	 * Adds the fieldName if not added before so it could be column name.
	 * 
	 * @param fieldName
	 */
	public static void addFieldName(String fieldName) {
		if (!ALLKEYS.contains(fieldName)) {
			ALLKEYS.add(fieldName);
			if ((ALLKEYS.size() - unshownKeys.size()) > 7) {
				unshownKeys.add(fieldName);
			}
		}
	}

	public static Collection<Descriptor> getRequiredDescriptors() {
		return descriptors.values();
	}

	public static Set<String> getRequiredDescriptorNames() {
		return descriptors.keySet();
	}

	public static Set<String> getOtherDescriptorNames() {
		Set<String> set = new HashSet<String>(ALLKEYS);
		set.removeAll(descriptors.keySet());
		return set;
	}

	public static Set<String> getOtherDescriptorNames(NcbcResource rsc) {
		Set<String> set = new HashSet<String>(rsc.getAllProperties().keySet());
		set.removeAll(descriptors.keySet());
		return set;
	}

	public static Descriptor getDescriptor(String name) {
		return descriptors.get(name);
	}

	public String getAvailableValues() {
		return null;
	}
}
