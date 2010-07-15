package edu.ucla.loni.ccb.itools.parser;

import org.jdom.Comment;
import org.jdom.Element;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * this class supports to output the resource as a LONI Pipeline module description.
 * Since 05/10/2009
 * @author qma
 *
 */
public class PipelineParser extends JdomResourceHelper {

	private static final String FULL_NAME = "fullName";
	private static final String PIPELLINE = "pipeline";
	private static final String VERSION = "version";
	private static final String MODUAL_GROUP = "moduleGroup";
	private static final String MODUAL = "modual";
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String LOCATION = "location";
	
	private static final String comment = "This is just a start, due to " +
			"the RDF format does not have enough tags for input/output, the result " +
			"may not be used directly by Pipeline";

	public static String getSaveString(NcbcResource rsc) {
		return element2String(resouce2Jdom(rsc));
	}

	public static Element resouce2Jdom(NcbcResource rsc) {
		Element ele = new Element(PIPELLINE);
		ele.addContent(new Comment(comment));
		ele.setAttribute(VERSION, ".1");
		Element mGroup = new Element(MODUAL_GROUP);
		ele.addContent(mGroup);
		
		Element module = new Element(MODUAL);
		module.setAttribute(NAME, rsc.getName());
		module.setAttribute(DESCRIPTION, rsc.getProperty(Descriptor.PROP_DESCRIPTION));
		module.setAttribute(LOCATION, "");
		module.setAttribute(VERSION, rsc.getProperty(Descriptor.PROP_VDS));
		
		Element executableAuthers = new Element("executableAuthors");
		mGroup.addContent(module);
		module.addContent(executableAuthers);
		
		Element auther = new Element("auther");
		executableAuthers.addContent(auther);
		String property = rsc.getProperty(Descriptor.PROP_AUTHORS);
		if (property != null) {
		    auther.setAttribute(FULL_NAME, property);
		}

		Element authers = new Element("authors");
		Element auther0 = new Element("auther");
		authers.addContent(auther0);
		auther0.setAttribute(FULL_NAME, "LONI iTools");
		auther0.setAttribute("email", "ivo@loni.ucla.edu");
		module.addContent(authers);
		
		Element citations = new Element("citations");
		Element citation = new Element("citation");
		
		module.addContent(citations);
		citations.addContent(citation);
		
		Element input = createIOElement("input", "Input", "0", "-input");
		
		module.addContent(input);
		Element output = createIOElement("output", "Output", "1", "-output");
		module.addContent(output);
		
		
		return ele;

	}
	
	private static Element createIOElement(String type, String name, String order, String switchStr) {
		Element ele = new Element(type);
		ele.setAttribute("name", name);
		ele.setAttribute("enabled", "true");
		ele.setAttribute("required", "true");
		setAttribute(ele,"order", order);
		setAttribute(ele,"switch",switchStr);
		ele.setAttribute("switchSpaced", "true");
		
		Element format = new Element("format");
		format.setAttribute("type","File");
		format.setAttribute("cardinality", "1");
		ele.addContent(format);
		
		Element fileTypes = new Element("fileTypes");
		Element fileType = new Element("filetype");
		fileType.setAttribute("name", "");
		fileType.setAttribute("extension", "");
		fileType.setAttribute(DESCRIPTION, "");
		
		format.addContent(fileTypes);
		fileTypes.addContent(fileType);
		
		return ele;

	}

}
