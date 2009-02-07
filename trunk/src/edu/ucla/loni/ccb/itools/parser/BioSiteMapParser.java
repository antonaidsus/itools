package edu.ucla.loni.ccb.itools.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

public class BioSiteMapParser extends JdomResourceHelper {
	private static final Logger logger = Logger.getLogger(BioSiteMapParser.class);
	private static final String RESOURCES = "resources";
	private static final String RESOURCE = "resource";
	private static final String rlsVersion = "rlsVersion";

	private static final String PROP_NAME = "name";
	private static final String PROP_DESCRIPTION = "description";
	private static final String PROP_ONTOLOGY = "ontologyLabel";
	private static final String PROP_AUTHORS = "authors";
	private static final String PROP_KEYWORDS = "keywords";
	private static final String PROP_TYPE = "resourceType";
	private static final String PROP_ORGANIZATION = "organization";
	private static final String PROP_URL = "url";
	private static final String PROP_DATAINPUT = "dataInput";
	private static final String PROP_DATAOUTPUT = "dataOutput";
	private static final String PROP_PLATFORMS = "platforms";
	private static final String PROP_LICENSE = "license";
	private static final String PROP_LANGUAGE = "language";
	private static final String PROP_CENTER = "center";
	private static final String PROP_RELEASE_DATE = "releaseDate";
	private static final String PROP_VERSION = "version";
	private static final String PROP_STAGE = "stage";

	private static HashMap<String, String> keysOtherNames = new LinkedHashMap<String, String>();

	private String name = "";
	private boolean forSandBox = true;

	static {
		keysOtherNames.put(PROP_NAME, Descriptor.PROP_NAME);
		keysOtherNames.put(PROP_DESCRIPTION, Descriptor.PROP_DESCRIPTION);
		keysOtherNames.put(PROP_ONTOLOGY, Descriptor.PROP_ONTOLOGY);
		keysOtherNames.put(PROP_AUTHORS, Descriptor.PROP_AUTHORS);
		keysOtherNames.put(PROP_KEYWORDS, Descriptor.PROP_KEYWORDS);
		keysOtherNames.put(PROP_URL, Descriptor.PROP_URL);
		keysOtherNames.put(PROP_TYPE, Descriptor.PROP_TYPE);
		keysOtherNames.put(PROP_VERSION, ResourceStandardizer.CSV_VERSION);
		keysOtherNames.put(PROP_RELEASE_DATE, ResourceStandardizer.CSV_DATE);
		keysOtherNames.put(PROP_STAGE, ResourceStandardizer.CSV_STAGE);
		keysOtherNames.put(PROP_LICENSE, Descriptor.PROP_LICENSE);
		keysOtherNames.put(PROP_LANGUAGE, Descriptor.PROP_LANGUAGE);
		keysOtherNames.put(PROP_DATAINPUT, Descriptor.PROP_INPUTDATA);
		keysOtherNames.put(PROP_DATAOUTPUT, Descriptor.PROP_OUTPUTDATA);
		keysOtherNames.put(PROP_PLATFORMS, Descriptor.PROP_PLATFORM);
		keysOtherNames.put(PROP_ORGANIZATION, Descriptor.PROP_ORGANIZATION);
	}

	public BioSiteMapParser() {
	}

	public BioSiteMapParser(String name) {
		this.name = name;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected void standardize(NcbcResource rsc) {
		ResourceStandardizer.standardize(rsc, keysOtherNames);
	}

	public NcbcResource jdom2Resource(Element ele) {
		NcbcResource rsc = new NcbcResource();
		rsc.setInSandBox(forSandBox);
		List<Attribute> attributes = getAttributes(ele);
		for (Attribute a : attributes) {
			if (a.getName().equals(PROP_CENTER)) {
				rsc.setCenter(a.getValue());
			}
		}
		for (Element prop : getChildren(ele)) {
			String name = prop.getName();
			if (name.equals(rlsVersion)) {
				jdom2Resource(prop, rsc);
			} else {
			   String text = prop.getText();
			   rsc.putProperty(name, text);
			}
		}
		if (rsc.getCenter() == null) {
			rsc.setCenter(name);
		}
		rsc.simplifyOntology();
		standardize(rsc);
		return rsc;
	}

	public static Element resouce2Jdom(NcbcResource rsc) {
		Element ele = new Element(RESOURCE);
		if (rsc.getCenter() != null) {
			ele.setAttribute("center", rsc.getCenter());
		}

		for (String propName : keysOtherNames.keySet()) {
			String value = getProperty(rsc, propName);
			if (value != null) {
				Element propE = new Element(propName);
				propE.setText(value);
				ele.addContent(propE);
			}
		}

		addVersion(ele, rsc);
		return ele;

	}

	private static void addVersion(Element ele, NcbcResource rsc) {
		String value = rsc.getProperty(Descriptor.PROP_VDS);
		if (value != null && !value.equals(",,")) {
			Element vdsE = new Element(rlsVersion);
			ele.addContent(vdsE);
			String[] vds = value.split(",", 3);
			if (vds.length > 0 && vds[0].length() != 0) {
				Element v = new Element(PROP_VERSION);
				v.setText(vds[0]);
				vdsE.addContent(v);
			}
			if (vds.length > 1 && vds[1].length() != 0) {
				Element v = new Element(PROP_RELEASE_DATE);
				v.setText(vds[1]);
				vdsE.addContent(v);
			}

			if (vds.length > 2 && vds[2].length() != 0) {
				Element v = new Element(PROP_STAGE);
				v.setText(vds[2]);
				vdsE.addContent(v);
			}
		}
	}

	public List<NcbcResource> parse(InputStream input) {
		List<NcbcResource> rv = Collections.emptyList();
		try {
			Document document = buildDocument(new InputStreamReader(input));
			setName(document.getRootElement().getAttributeValue("name"));
			List<Element> children = getChildren(document.getRootElement());
			rv = new ArrayList<NcbcResource>(children.size());
			for (Element child : children) {
				if (child.getName().equals(RESOURCE)) {
					NcbcResource rsc = jdom2Resource(child);
					if (rsc.isValid()) {
					    rv.add(rsc);
					} else {
						logger.warn("The following resource is not valid\n" +element2String(child));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rv;
	}

	private static String getProperty(NcbcResource rsc, String key) {
		return rsc.getProperty((String) keysOtherNames.get(key));
	}

	public static String getSaveString(Collection<NcbcResource> list) {
		Element rdf = new Element(RESOURCES);
		for (Iterator<NcbcResource> i = list.iterator(); i.hasNext();) {
			rdf.addContent(resouce2Jdom((NcbcResource) i.next()));
		}
		return element2String(rdf);
	}

	public static String getSaveString(NcbcResource rsc) {
		return element2String(resouce2Jdom(rsc));
	}

	public boolean isForSandBox() {
		return forSandBox;
	}

	public void setForSandBox(boolean forSandBox) {
		this.forSandBox = forSandBox;
	}

}
