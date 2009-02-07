package edu.ucla.loni.ccb.itools.view;

import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * This should be final format of the indexTree, and ontology
 * organizer. It will include all resources.
 * @author Qunfei Ma
 *
 */
public class NcbcResourceIndexTree extends ResourceIndexTree {
	/** Logger for this class*/
    public static final Logger LOGGER = Logger.getLogger(NcbcResourceIndexTree.class);
    
    public NcbcResourceIndexTree(StandardResourceDisplayer displayer) {
		super(displayer, "NCBC");
	}

	public void load() {
    	loadOnTology();
    	List<NcbcResource> rscs = DaoFactory.getDaoFactory().getResourceDao().getAllResources();
		addResources(rscs);
    	LOGGER.debug(rscs.size() + " was added to NcbcTree");
    	((DefaultTreeModel)getModel()).reload();
    	setInitialized(true);
    }
    
    public void updateFromInternet () {
    	getResources().clear();
		getRoot().removeAllChildren();
		loadOnTology();
		addResources(DaoFactory.getDaoFactory().getResourceDao().getAllResources());
		setRoot(getRoot());

		MainFrame.getContent().setStatus("update finished from: Allcenters");
    }
}
