package edu.ucla.loni.ccb.itools.view;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	private static Map dictionary = new HashMap();

	public static void putValues(Object key, Collection values) {
		Collection v = getValues(key);
		v.addAll(values);
	}

	public static Collection getValues(Object key) {
		Collection v = (Collection) dictionary.get(key);
		if (v == null) {
			v = new HashSet();
			dictionary.put(key, v);
		}
		return v;
	}

	public static void addValue(NcbcResource[] rscs) {
		for (int i = 0; i < rscs.length; i++) {
			addValue(rscs[i]);
		}
	}

	public static void addValue(Collection rscs) {
		for (Iterator i = rscs.iterator(); i.hasNext();) {
			addValue((NcbcResource) i.next());
		}
	}

	public static void addValue(NcbcResource rsc) {
		Collection requiredNames = Descriptor.getRequiredDescriptors();
		for (Iterator iter = requiredNames.iterator(); iter.hasNext();) {
			Descriptor element = (Descriptor) iter.next();
			/* if (element.isSingleline()) */{
				String name = element.getName();
				String value = rsc.getProperty(name);
				if (value != null && value.length() > 0) {
					String[] items = value.split("\\s|,");
					putValues(name, Arrays.asList(items));
				}
			}
		}
	}
}
