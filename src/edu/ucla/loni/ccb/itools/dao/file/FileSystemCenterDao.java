package edu.ucla.loni.ccb.itools.dao.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.dao.CenterDao;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.parser.CentersParser;

public class FileSystemCenterDao implements CenterDao {
    public static final Logger LOGGER = Logger.getLogger(FileSystemCenterDao.class);
    Collator usCollator = Collator.getInstance(Locale.US);
    
	List centers = new ArrayList(0);
	String filename = "ncbcCenters.xml";
	File file;
	
	public FileSystemCenterDao() {
		usCollator.setStrength(Collator.PRIMARY);
		String dir = NcbcResourceIO.getResourceSaveDir();
		file = new File(dir, filename);
		try {
			InputStream inputS;
			if (file.exists()) {
				inputS = new FileInputStream(file);
				LOGGER.debug("read centers from filesystem");
			} else {
				inputS = FileSystemCenterDao.class.getResourceAsStream("/" +filename);
				LOGGER.debug("read centers from classpath");
			}
			centers = CentersParser.parse(inputS);
		} catch (FileNotFoundException e) {
			LOGGER.info(e);
		}
	}

	public void addExternalCenter(String centerName) {
		if (getCenter(centerName) != null) {
			return;
		}
		
		NcbcCenter center = new NcbcCenter();
		center.setName(centerName);
		center.setExternal(true);
		centers.add(center);
		save();
	}


	public void save() {
		NcbcResourceIO.writeStr2File(CentersParser.toXml(centers), file);
	}
	
	public NcbcCenter getCenter(String name) {
		for (Iterator iter = centers.iterator(); iter.hasNext();) {
			NcbcCenter center = (NcbcCenter) iter.next();
			if (usCollator.equals(center.getName(), name) ||
					usCollator.equals(center.getName() + "Center", name)) {
				return center;
			}			
		}
		return null;
	}

	public List getCenters() {
		return centers;
	}
}
