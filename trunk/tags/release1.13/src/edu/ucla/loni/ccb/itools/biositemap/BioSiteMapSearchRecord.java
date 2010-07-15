package edu.ucla.loni.ccb.itools.biositemap;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BioSiteMapSearchRecord {
	private static final Logger logger = Logger.getLogger(BioSiteMapSearchRecord.class);

	private String name;
	private ArrayList<Record> records = new ArrayList<Record>();
	
	public static class Record {
		public String actionTime;
		public String modifiedTime;
		public String url;
		public int numberOfResource;
		
		public Record(String url, String updateTime, String modifiedTime, int number) {
			this.url = url;
			this.actionTime = updateTime;
			this.modifiedTime = modifiedTime;
			numberOfResource = number;
		}
	}
	
	public BioSiteMapSearchRecord(String name) {
		this.name = name;
	}
	/**
	 * this should be used when updated from search results only
	 * @param url
	 * @param modifiedTime
	 */
	public boolean add(String url, String actionTime, String modifiedTime, int numberOfRsc, boolean forcedUpdate) {
		Record record = new Record(url, actionTime, modifiedTime, numberOfRsc);

		int size = records.size();
		if (size == 0 || forcedUpdate || updated(records.get(size -1), record)) {
			record.actionTime = new Date().toString();
			records.add(record);
			return true;
		}
		return false;
	}
	/**
	 * this method should be used when parsing the xml.
	 */
	public void add(Record record) {
		records.add(record);
	}
	
    private boolean updated (Record oldone, Record newone) {
    	if (newone.modifiedTime == null) {
    		return true;
    	}
    	boolean b = true;
		try {
			b = Integer.parseInt(newone.modifiedTime) > Integer.parseInt(oldone.modifiedTime);
		} catch (NumberFormatException e) {
			logger.warn(e);
		}
		return b;
    }
    
	public String getName() {
		return name;
	}
	public List<Record> getRecords() {
		return records;
	}
}
