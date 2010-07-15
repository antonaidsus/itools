package edu.ucla.loni.ccb.itools.dao.file;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchHistory;
import edu.ucla.loni.ccb.itools.dao.WebSearchHistoryDao;
import edu.ucla.loni.ccb.itools.parser.BioSiteMapSearchHistoryParser;

public class FileSystemWebSearchHistoryDao implements WebSearchHistoryDao {
	Logger logger = Logger.getLogger(FileSystemWebSearchHistoryDao.class);
	
	BioSiteMapSearchHistory history;
	String filename = "BioSiteMapSearchHistory.xml";
	File file;
	
	public FileSystemWebSearchHistoryDao() {
		String dir = NcbcResourceIO.getResourceSaveDir();
		file = new File(dir, filename);
	}
	
	public BioSiteMapSearchHistory getHistory() {
		if (history == null) {
			if (!file.exists()) {
				history = new BioSiteMapSearchHistory();
				return history; 
			}
			try {
				history = BioSiteMapSearchHistoryParser.parse(new FileInputStream(file));
			} catch (Exception e) {
				logger.error("reading history file from " + file.getName(), e);
			}
		}
		return history;
	}

	public void save2History(BioSiteMapSearchHistory history) {
		NcbcResourceIO.writeStr2File(
				BioSiteMapSearchHistoryParser.toXML(history), file);
		history = null;
		
	}

}
