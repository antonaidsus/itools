package edu.ucla.loni.ccb.itools.view;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * This class keep all the dictionaries of all possible values for specific item
 * (each required Descriptor, such as Name, Description).
 * 
 * @author Qunfei Ma
 * 
 */
public class Dictionary {
	private static Map<Object, Collection<String>> dictionary = new HashMap<Object, Collection<String>>();

	public static void putValues(Object key, Collection<String> values) {
		Collection<String> v = getValues(key);
		v.addAll(values);
	}

	public static Collection<String> getValues(Object key) {
		Collection<String> v = dictionary.get(key);
		if (v == null) {
			v = new HashSet<String>();
			dictionary.put(key, v);
		}
		return v;
	}

	public static void addValue(NcbcResource[] rscs) {
		for (int i = 0; i < rscs.length; i++) {
			addValue(rscs[i]);
		}
	}

	public static void addValue(Collection<NcbcResource> rscs) {
		for (NcbcResource rsc : rscs) {
			addValue(rsc);
		}
	}

	public static void addValue(NcbcResource rsc) {
		Collection<Descriptor> requiredNames = Descriptor
				.getRequiredDescriptors();
		for (Descriptor element : requiredNames) {
			String name = element.getName();
			String value = rsc.getProperty(name);
			if (value != null && value.length() > 0) {
				String[] items = value.split("\\s|,");
				putValues(name, Arrays.asList(items));
			}
		}
	}
}
