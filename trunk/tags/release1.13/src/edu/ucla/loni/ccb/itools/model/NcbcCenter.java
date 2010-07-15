package edu.ucla.loni.ccb.itools.model;

/**
 * This class represents a Ncbc center.
 * It will be created by CenterParser by reading centers.xml
 * @author Jeff Qunfei Ma
 *
 */
public class NcbcCenter {
	private String name;
	private String url;
	private String homeUrl;
	private boolean external;
	private String fullName;
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHomeUrl() {
		return homeUrl;
	}
	public void setHomeUrl(String homeurl) {
		this.homeUrl = homeurl;
	}
	public boolean isExternal() {
		return external;
	}
	public void setExternal(boolean external) {
		this.external = external;
	}
	
	public String toString() {
		return getName() + (isExternal() ? "(External)" : "");
	}
	
}
