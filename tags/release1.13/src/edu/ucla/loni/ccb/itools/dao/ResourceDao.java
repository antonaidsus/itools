package edu.ucla.loni.ccb.itools.dao;

import java.util.List;

import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * @author Jeff Qunfei Ma
 *
 */
public interface ResourceDao {
	
	/**
	 * Adds the resource to the data system. 
	 * @param rsc the NcbcResource to save.
	 */
	public void addResource(NcbcResource rsc);
	
	/**
	 * rsc was modified, update to the data system.
	 * @param rsc
	 */
	public void updateResource(NcbcResource rsc);
	
	/**
	 * Remove the rsc from data system.
	 * @param rsc
	 */
	public void removeResource(NcbcResource rsc);

	/**
	 * @param reg
	 * @param category
	 * @return all Resources match the search.
	 */
	public List<NcbcResource> getResources(String reg, String category);
	
	/**
	 * @param reg
	 * @param category
	 * @param center
	 * @return all Resources match the search, which is defined in the center.
	 */
	public List<NcbcResource> getResources(String reg, String category, String center);
	
	/**
	 * @return All resources from database.
	 */
	public List<NcbcResource> getAllResources();
	

	/**
	 * Loads resources into memory for the center.
	 * Each center have a different name.
	 * @param center
	 * @return
	 */
	public List<NcbcResource> load(String center);

	/**
	 * Sets and save resources to the database. All resources should have the same
	 * center, and the center's data will be removed first.
	 * For server/client this is too aggressive, one user could overwrite other's 
	 * @param center the center's name
	 */
	public void setResources(List<NcbcResource> rscs);

	public boolean canRecover(String center);
	public List<NcbcResource> recover(String center);
	public String getOntologyString();
	public void setOntologyString(String ontology);
}
