package edu.ucla.loni.ccb.itools.dao;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import edu.ucla.loni.ccb.itools.Util;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * THis class just keep some method which I could not find a better place.
 * @author Jeff Qunfei Ma
 *
 */
public abstract class DaoHelper {

	public static List<NcbcResource> searchResources(String reg, String category, List<NcbcResource> candidates) {
		if ("*".equals(category)) {
			return candidates;
		}
		reg = Util.toJavaStyle(reg);
		LinkedList<NcbcResource> rscs = new LinkedList<NcbcResource>();
		Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		for (NcbcResource next : candidates) {
			String value = (next).getProperty(category);
			if (value == null || value.length() == 0) continue;
			if (p.matcher(value).find() || p.matcher(value).matches()) {
				rscs.add(next);
			}
		}
		return rscs;
	}

}
