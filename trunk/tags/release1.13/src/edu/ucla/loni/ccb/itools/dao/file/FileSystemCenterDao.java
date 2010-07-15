package edu.ucla.loni.ccb.itools.dao.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.DataCache;
import edu.ucla.loni.ccb.itools.dao.CenterDao;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.parser.CentersParser;

public class FileSystemCenterDao implements CenterDao {
    private static final Logger logger = Logger.getLogger(FileSystemCenterDao.class);    
	List<NcbcCenter> centers = new ArrayList<NcbcCenter>(0);
	String filename = "ncbcCenters.xml";
	File file;
	
	public FileSystemCenterDao() {
		String dir = NcbcResourceIO.getResourceSaveDir();
		file = new File(dir, filename);
		try {
			InputStream inputS;
			if (file.exists()) {
				inputS = new FileInputStream(file);
				logger.debug("read centers from filesystem");
			} else {
				inputS = FileSystemCenterDao.class.getResourceAsStream("/" +filename);
				logger.debug("read centers from classpath");
			}
			centers = CentersParser.parse(inputS);
		} catch (FileNotFoundException e) {
			logger.info(e);
		}
	}

	public boolean addExternalCenter(String centerName) {
		if (getCenter(centerName) != null) {
			return false;
		}
		NcbcCenter center = new NcbcCenter();
		center.setName(centerName);
		center.setExternal(true);
		centers.add(center);
		logger.info("New center defined for '" + centerName + "'");
		save();
		return true;
	}


	public void save() {
		NcbcResourceIO.writeStr2File(CentersParser.toXml(centers), file);
	}
	
	public NcbcCenter getCenter(String name) {
		for (NcbcCenter center : centers) {
			if (DataCache.centerMatchByName(center, name)) {
				return center;
			}			
		}

		logger.info("No center defined for '" + name + "'");
		return null;
	}

	public List<NcbcCenter> getCenters() {
		return centers;
	}

	public boolean deleteCenter(String centerName) {
		deleteCenterInMemory(centerName);
		save();
		return true;
	}

	private void deleteCenterInMemory(String centerName) {
		for (Iterator<NcbcCenter> i = centers.iterator(); i.hasNext();) {
			NcbcCenter center = i.next();
			if (DataCache.centerMatchByName(center, centerName)) {
				i.remove();
				break;
			}
		}
	}

	public boolean updateCenter(NcbcCenter center) {
		deleteCenterInMemory(center.getName());
		centers.add(center);
		save();	
		return true;
	}
}
