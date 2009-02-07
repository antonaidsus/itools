package edu.ucla.loni.ccb.itools.biositemap;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class BioSiteMapSearchRecord {
	private String name;
	private ArrayList<Record> records = new ArrayList<Record>();
	
	public static class Record {
		String actionTime;
		String modifiedTime;
		String url;
		int numberOfResource;
		
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
		if (size == 0 || forcedUpdate || updated((Record) records.get(size -1), record)) {
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
    	return Integer.parseInt(newone.modifiedTime) >
    	Integer.parseInt(oldone.modifiedTime);
    }
    
    public String toXML(String tab) {
    	StringBuffer bf = new StringBuffer(tab + "<bioSiteMap name=\"" + name +"\" >\n");
    	for (Iterator iter = records.iterator(); iter.hasNext();) {
			Record record = (Record) iter.next();
			bf.append(tab + "    " + "<history url=\"" + record.url + "\" lastModified=\"" + record.modifiedTime 
					+ "\" numberOfResource =\"" + record.numberOfResource 
					+ "\" updateTime=\"" + record.actionTime + "\"/>\n");
    	}
		bf.append(tab + "</bioSiteMap>\n");
		return bf.toString();

    }
	public String getName() {
		return name;
	}
}
