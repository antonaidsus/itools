package edu.ucla.loni.ccb.itools.parser;

import java.io.InputStream;
import java.util.List;

import org.jdom.Element;

import edu.ucla.loni.ccb.itools.model.NcbcResource;


public abstract class JdomResourceHelper extends JdomHelper {
	
	public List<NcbcResource> parse(InputStream input) {
		return null;
	}

	protected void jdom2Resource(Element ele, NcbcResource rsc) {
		for (Element prop : getChildren(ele)) {			
			String name = prop.getName();
			String text = prop.getText();
			rsc.putProperty(name, text);
		}
	}
}
