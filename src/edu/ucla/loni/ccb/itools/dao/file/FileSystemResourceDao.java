package edu.ucla.loni.ccb.itools.dao.file;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.dao.DaoHelper;
import edu.ucla.loni.ccb.itools.dao.ResourceDao;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

public class FileSystemResourceDao implements ResourceDao {
	public static final Logger LOGGER = Logger.getLogger(FileSystemResourceDao.class);
	
	public void addResource(NcbcResource rsc) {
		NcbcResourceIO.saveResource(rsc);
	}
	
	public void updateResource(NcbcResource rsc) {
		NcbcResourceIO.saveResource(rsc);	
	}

	public void removeResource(NcbcResource rsc) {
		NcbcResourceIO.removeResource(rsc);		
	}

	public List<NcbcResource> getResources(String reg, String category) {
		return DaoHelper.searchResources(reg, category, NcbcResourceIO.getAllResources());
	}
	
	public List<NcbcResource> getResources(String reg, String category, String center) {
		return DaoHelper.searchResources(reg, category, NcbcResourceIO.getResources(center));
	}
	
	public List<NcbcResource> getAllResources() {
		return NcbcResourceIO.getAllResources();
	}

    public List<NcbcResource> load(String center) {
		return NcbcResourceIO.getResources(center);
	}
	
	public void save(String center) {
		NcbcResourceIO.saveCenter(center);
	}
	
	public boolean canRecover(String center) {
		File file = new File(NcbcResourceIO.center2Filename(center, true));
		LOGGER.debug(center + " can recover: " + file.exists());
		return file.exists();
	}
	
	public List<NcbcResource> recover(String center) {
		return NcbcResourceIO.recover(center);
	}

	public void setResources(List<NcbcResource> rscs) {
		if (rscs.size() == 0) {
			return;
		}
		NcbcResource object = rscs.get(0);
		String center = object.getCenter();
		NcbcResourceIO.backUp(center);
		NcbcResourceIO.putResources(NcbcResourceIO.center2Filename(center), rscs);
		save(center);		
	}
}
