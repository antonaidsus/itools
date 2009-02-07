package edu.ucla.loni.ccb.itools.dao;

import java.util.List;

import edu.ucla.loni.ccb.itools.model.NcbcCenter;


public interface CenterDao {
	public List<NcbcCenter> getCenters();
	public NcbcCenter getCenter(String nameName);
	void addExternalCenter(String centerName);
}
