package edu.ucla.loni.ccb.itools.view;

import java.util.List;

import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * This interface mainly used to help setup left side Pane. Usually its
 * implementation is a JTree, but for IATR center, it has to be a JPanel. For
 * convenience, I created this interface.
 * 
 * @author Jeff Ma
 * 
 */
public interface IResourceContainer {
	/**
	 * @return all Resources it has
	 */
	public List<NcbcResource> getResources();

	/**
	 * @return its name, which distinguish each other.
	 */
	public String getName();

	public void setName(String name);

	/**
	 * Load into resources
	 */
	public void load();

	public String getUrlString();

	public void setUrlString(String urlstring);

	public void addResource(NcbcResource rsc);
	
	public void reload();
}
