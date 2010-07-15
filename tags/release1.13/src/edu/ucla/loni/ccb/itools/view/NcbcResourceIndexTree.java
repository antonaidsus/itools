package edu.ucla.loni.ccb.itools.view;

import java.util.List;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.DataBank;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * This class displays all the resources regardless of centers.
 * That means it displays the sum of all the centers.
 * @author Qunfei Ma
 *
 */
public class NcbcResourceIndexTree extends ResourceIndexTree {
	private static final long serialVersionUID = 1L;
	/** Logger for this class*/
    public static final Logger logger = Logger.getLogger(NcbcResourceIndexTree.class);
    
    public NcbcResourceIndexTree(StandardResourceDisplayer displayer, NcbcCenter center) {
		super(displayer, center);
	}
    
	protected List<NcbcResource> getResourcesFromDao() {
		return DataBank.getAllResources();		
	}

    public void updateFromInternet () {
    	getResources().clear();
		getRoot().removeAllChildren();
		loadOnTology();
		addResources(DataBank.getAllResources());
		setRoot(getRoot());

		MainFrame.getContent().setStatus("update finished from: Allcenters");
    }
}
