package edu.ucla.loni.ccb.itools.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.CSVParser;
import edu.ucla.loni.ccb.itools.parser.IatrResourceParser;

/**
 * This class contains and manages the resources defined by
 * iatr.csv.
 * 
 * @author Qunfei Ma
 *
 */
public class IatrResourceIndexTree extends ResourceIndexTree {
	private String ontology = "";
	private IatrPane iatrPane;
	
	public IatrResourceIndexTree(StandardResourceDisplayer displayer) {
		super(displayer,"IATR");
	}
	
	public void load() {
    	loadResources();
		if (iatrPane != null) {
			iatrPane.treeLoaded();
		} else {
			((DefaultTreeModel)getModel()).reload();
		}
    }
    
	protected List<NcbcResource> readFromUrl(String urlStr) throws IOException, MalformedURLException {
		List<NcbcResource> resources;
		if (urlStr.endsWith(".csv")) {
			resources = CSVParser.parse(new URL(urlStr).openStream());
		} else {
			resources = IatrResourceParser.parseXML(new URL(urlStr).openStream());
		}
		return resources;
	}

    public void ontologyChanged(String ontology) {
    	this.ontology  = ontology;
    	clear();
    	for (NcbcResource element :  allResources) {
			addResource(element); //this why uses set			
		}
    }
	
    /**
     * Adds a node to the tree, the node created from the String. 
     * @param filename the name of the file from which the PMData be created
     */
    public void addResource(NcbcResource ncbcr) {
    	if (displayer.isSandBox != ncbcr.isInSandBox()) {
    		return; //should be added to another dislayer
    	}
    	allResources.add(ncbcr);
    	String ontology = getOntology(ncbcr);
        if (ontology.indexOf("-->") != -1) {
        	ontology = ontology.replaceAll("-->", Descriptor.DELIMITER);
        }
        if (ontology.indexOf("->") != -1) {
        	ontology = ontology.replaceAll("-->", Descriptor.DELIMITER);
        }
    	String[] tokens = ontology.split(Descriptor.DELIMITER);
    	DefaultMutableTreeNode node = addNode2Tree(getRoot(), tokens, 0);
    	node = addNode2Tree(node, ncbcr.getName());
    	node.setAllowsChildren(false);
    	((UB)node.getUserObject()).data = ncbcr;

    }
    /* because the IATR's ontology may dynamicaly changed, so it is special*/
    private String getOntology(NcbcResource ncbcr) {  
    	String str = ncbcr.getProperty(Descriptor.PROP_ONTOLOGY);
    	if (str != null && str.length() > 0) {
    		return str;
    	}
    	
    	str = ontology;
    	if (str.trim().length() == 0) {
    		return Descriptor.NOVALUE;
    	}
		String[] items = str.split(Descriptor.DELIMITER);
		
		String s = ncbcr.getProperty(items[0]);
		if (s == null || s.equals("")) {
			return Descriptor.NOVALUE; //this should no be case, ontology must be required field.
		}
		StringBuffer buff = new StringBuffer(s);
		for (int i = 1; i < items.length; i++) {
			s = ncbcr.getProperty(items[i]);
			if (s == null || s.equals("")) {
				s = Descriptor.NOVALUE; //this should no be case, ontology must be required field.
			}
			buff.append(Descriptor.DELIMITER + s);
		}
		return buff.toString();
	}
    
	public void setPane(IatrPane pane) {
		iatrPane = pane;		
	}
}
