package edu.ucla.loni.ccb.itools.model;

/**
 * this class represents Ncbc center.
 * It will be created by CenterParser by reading centers.xml
 * @author Jeff Qunfei Ma
 *
 */
public class NcbcCenter {
	private String name;
	private String url;
	private String homeurl;
	private boolean external;
	
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
		return homeurl;
	}
	public void setHomeUrl(String homeurl) {
		this.homeurl = homeurl;
	}
	public boolean isExternal() {
		return external;
	}
	public void setExternal(boolean external) {
		this.external = external;
	}
	
}
