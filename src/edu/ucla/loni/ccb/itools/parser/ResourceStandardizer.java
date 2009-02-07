package edu.ucla.loni.ccb.itools.parser;

import java.util.Iterator;
import java.util.Map;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * This class tries to make resource to use standard attribute name. E.G. It
 * will modify the attribute "Key Words" to "Keywords"
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class ResourceStandardizer {
	public static final String CSV_CREATOR = "Creator";
	public static final String CSV_PLATFORM = "Platforms";
	public static final String CSV_STATUS = "Status";
	public static final String CSV_STAGE = "Stage";
	public final static String CSV_VERSION = "Version";
	public final static String CSV_DATE = "Date";
	public final static String CSV_WWW = "WWW";
	public final static String KEYWOORD = "Key Words";

	public static void standardizeVersion(NcbcResource r) {
		Map<String, String> p = r.getAllProperties();
		if (p.get(Descriptor.PROP_VDS) == null) {
			// Version, Date, Stage
			String version = (String) p.remove(CSV_VERSION);
			if (version == null)
				version = "";
			String date = (String) p.remove(CSV_DATE);
			date = date == null ? "" : date.replaceAll(",", "/");
			String status = (String) p.remove(CSV_STATUS);
			if (status == null) {
				status = (String) p.remove(CSV_STAGE);
			}
			if (status == null) {
				status = "";
			}
			p.put(Descriptor.PROP_VDS, version + "," + date + "," + status);
		}

	}

	public static void standardize(NcbcResource rsc, Map<String, String> nameMap) {
		for (Iterator<String> i = nameMap.keySet().iterator(); i.hasNext();) {
			String localName = (String) i.next();
			String object = rsc.getAllProperties().remove(localName);
			if (object != null) {
				rsc.getAllProperties().put(nameMap.get(localName), object);
			}
		}

		standardizeVersion(rsc);
	}

	public static void standardize(NcbcResource r) {
		standardizeVersion(r);

		Map<String, String> p = r.getAllProperties();
		for (int i = 0; i < xml_modifies.length; i++) {
			String value = p.remove(xml_modifies[i][0]);
			if (value != null) {
				p.put(xml_modifies[i][1], value);
			}
		}
	}

	private static String[][] xml_modifies = {
			{ "name", Descriptor.PROP_NAME },
			{ "description", Descriptor.PROP_DESCRIPTION },
			{ "keyword", Descriptor.PROP_KEYWORDS },
			{ "Key Words", Descriptor.PROP_KEYWORDS },
			{ "www", Descriptor.PROP_URL }, { "WWW", Descriptor.PROP_URL },
			{ "platform", Descriptor.PROP_PLATFORM },
			{ "Platforms", Descriptor.PROP_PLATFORM },
			{ "creator", Descriptor.PROP_AUTHORS },
			{ "Creator", Descriptor.PROP_AUTHORS },
			{ "license", Descriptor.PROP_LICENSE } };
}
