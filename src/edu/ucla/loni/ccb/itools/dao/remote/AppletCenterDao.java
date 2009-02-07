package edu.ucla.loni.ccb.itools.dao.remote;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;

import edu.ucla.loni.ccb.itools.dao.CenterDao;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.parser.CentersParser;

public class AppletCenterDao implements CenterDao {
	List centers;

	public void addExternalCenter(String centerName) {
		// TODO Auto-generated method stub
	}

	public NcbcCenter getCenter(String nameName) {
		if (centers == null) {
			centers = getCenters();
		}
		for (Iterator iter = centers.iterator(); iter.hasNext();) {
			NcbcCenter center = (NcbcCenter) iter.next();
			if (center.getName().equals(nameName)) {
				return center;
			}
		}
		return null;

	}

	public List getCenters() {
		if (centers != null) {
			return centers;
		}
		String str = ProxyServer.talk2Server("resource.htm?method=getCenters");
		return CentersParser.parse(new ByteArrayInputStream(str.getBytes()));
	}

}
