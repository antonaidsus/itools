package edu.ucla.loni.ccb.itools.dao.remote;

import java.io.ByteArrayInputStream;
import java.util.List;

import edu.ucla.loni.ccb.itools.dao.CenterDao;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.parser.CentersParser;

public class AppletCenterDao implements CenterDao {
	List<NcbcCenter> centers;

	public boolean addExternalCenter(String centerName) {
		return false;
	}

	public NcbcCenter getCenter(String nameName) {
		if (centers == null) {
			centers = getCenters();
		}
		for (NcbcCenter center : centers) {
			if (center.getName().equals(nameName)) {
				return center;
			}
		}
		return null;

	}

	public List<NcbcCenter> getCenters() {
		if (centers != null) {
			return centers;
		}
		String str = ProxyServer.talk2Server("resource.htm?method=getCenters");
		return CentersParser.parse(new ByteArrayInputStream(str.getBytes()));
	}

	public boolean deleteCenter(String centerName) {
		return false;
		
	}

	public boolean updateCenter(NcbcCenter center) {
		return false;
	}

}
