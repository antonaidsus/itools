package edu.ucla.loni.ccb.itools.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;

public class RdfParser extends JdomResourceHelper {
	private static final Logger logger = Logger.getLogger(RdfParser.class);

	public static final Namespace NS_BRO = Namespace
			.getNamespace("BRO",
					"http://bioontology.org/ontologies/BiomedicalResourceOntology.owl#");
	public static final Namespace NS_XSP = Namespace.getNamespace("xsp",
			"http://www.owl-ontologies.com/2005/08/07/xsp.owl#");
	public static final Namespace NS_XSD = Namespace.getNamespace("xsd",
			"http://www.w3.org/2001/XMLSchema#");
	public static final Namespace NS_RDFS = Namespace.getNamespace("rdfs",
			"http://www.w3.org/2000/01/rdf-schema#");

	public static final Namespace NS_OWL = Namespace.getNamespace("owl",
			"http://www.w3.org/2002/07/owl#");
	public static final Namespace NS_BASE = Namespace.getNamespace("base",
			"http://www.bioontologies.org/biositemaps/NCBO.rdf");

	public static final Namespace NS_PROTEGE = Namespace.getNamespace(
			"protege", "http://protege.stanford.edu/plugins/owl/protege#");

	public static final Namespace NS_DESC = Namespace.getNamespace("desc",
			"http://bioontology.org/ontologies/biositemap.owl#");
	public static final Namespace NS_RDF = Namespace.getNamespace("rdf",
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#");

	static final String RESOURCES = "RDF";
	static final String RESOURCE = "Resource_Description";

	static final String PROP_NAME = "resource_name";
	static final String PROP_DESCRIPTION = "description";
	static final String PROP_AUTHORS = "contact_person";
	static final String PROP_KEYWORDS = "keywords";
	static final String PROP_TYPE = "resource_type";
	static final String PROP_ORGANIZATION = "organization";
	static final String PROP_URL = "URL";
	static final String PROP_DATAINPUT = "data_input";
	static final String PROP_DATAOUTPUT = "data_output";
	static final String PROP_PLATFORMS = "platforms";
	static final String PROP_LICENSE = "license";
	static final String PROP_VERSION_INFORMATION = "version_information";
	static final String PROP_LANGUAGE = "language";
	static final String PROP_BYLINE = "byline";
	static final String PROP_CENTER = "center";
	static final String PROP_VERSION_INFORMATIONL = "Version_Information";
	static final String PROP_RELEASE_DATE = "release_date";
	static final String PROP_VERSION = "version";
	static final String PROP_STAGE = "development_stage";
	
	static final String PROP_IDENTIFIER = "Identifier";
	static final String PROP_AUTHORS_EMAIL = "contact_person_email";

	static Map<String, String> keysOtherNames = new HashMap<String, String>();
	static Set<String> infomationModel = Collections.<String>emptySet();

	private String name;
	private boolean forSandBox = true;
	
	static {
		initInformationModel();
		
		keysOtherNames.put(PROP_NAME, Descriptor.PROP_NAME);
		keysOtherNames.put(PROP_DESCRIPTION, Descriptor.PROP_DESCRIPTION);
		keysOtherNames.put(PROP_AUTHORS, Descriptor.PROP_AUTHORS);
		keysOtherNames.put(PROP_KEYWORDS, Descriptor.PROP_KEYWORDS);
		keysOtherNames.put(PROP_URL, Descriptor.PROP_URL);
		keysOtherNames.put(PROP_TYPE, Descriptor.PROP_ONTOLOGY);
		keysOtherNames.put(PROP_VERSION, ResourceStandardizer.CSV_VERSION);
		keysOtherNames.put(PROP_RELEASE_DATE, ResourceStandardizer.CSV_DATE);
		keysOtherNames.put(PROP_STAGE, ResourceStandardizer.CSV_STAGE);
		keysOtherNames.put(PROP_LICENSE, Descriptor.PROP_LICENSE);
		keysOtherNames.put(PROP_LANGUAGE, Descriptor.PROP_LANGUAGE);
		keysOtherNames.put(PROP_DATAINPUT, Descriptor.PROP_INPUTDATA);
		keysOtherNames.put(PROP_DATAOUTPUT, Descriptor.PROP_OUTPUTDATA);
		keysOtherNames.put(PROP_PLATFORMS, Descriptor.PROP_PLATFORM);
		keysOtherNames.put(PROP_ORGANIZATION, Descriptor.PROP_ORGANIZATION);
		
		infomationModel.addAll(keysOtherNames.keySet());
	}

	public NcbcResource jdom2Resource(Element ele) {
		NcbcResource rsc = new NcbcResource();
		rsc.setInSandBox(forSandBox);
		StringBuffer ontology = new StringBuffer();
		for (Element prop : getChildren(ele)) {
			String name = prop.getName();
			String text = prop.getText();
			if (name.equals(PROP_CENTER) /*|| name.equals(PROP_ORGANIZATION)*/) {
				rsc.setCenter(text);
			} else if (name.equals(PROP_VERSION_INFORMATION)) {
				List<Element> children = getChildren(prop);
				if (!children.isEmpty()) {
					Element vdsE = (Element) children.get(0);
					jdom2Resource(vdsE, rsc);
				}
			} else if (name.equals(PROP_TYPE)) {
				if (ontology.length() != 0) {
					ontology.append(";");
				}
				List<Element> type =getChildren(prop);
				if (type.size() != 0) {					
					ontology.append(type.get(0).getName());
				}
			} else {
				rsc.putProperty(name, text);
			}
		}
		if (rsc.getCenter() == null) {
			rsc.setCenter(name);
		}
		rsc.putProperty(Descriptor.PROP_ONTOLOGY, ontology.toString());
		ResourceStandardizer.standardize(rsc, keysOtherNames);
		return rsc;
	}

	private static void initInformationModel() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					RdfParser.class.getResourceAsStream(
							"/BiomedicalResourceInformationModel.txt")));
			infomationModel = new HashSet<String>(30);
			String line = null;			
			while ( (line = br.readLine()) != null) {
				infomationModel.add(line);
			}
		} catch (Exception e) {
			logger.warn("Failed to read IM", e);
		}		
	}

	private static String getProperty(NcbcResource rsc, String key) {
		String otherName = keysOtherNames.get(key);
		if (otherName != null) {
			key = otherName;
		}
		return rsc.getProperty(key);
	}

	public static String getSaveString(Collection<NcbcResource> resources) {
		Element rdf = new Element(RESOURCES, NS_RDF);
		for (NcbcResource rsc : resources) {
			rdf.addContent(resouce2Jdom(rsc));
		}
		return element2String(rdf);
	}

	public List<NcbcResource> parse(InputStream input) {
		List<NcbcResource> rv = Collections.emptyList();
		try {
			Document document = buildDocument(new InputStreamReader(input));
			Element rootElement = document.getRootElement();
			String base = rootElement.getAttributeValue("base", Namespace.XML_NAMESPACE);

			if (base != null) {
				int index = base.lastIndexOf("/") + 1;
				name = base.substring(index);
				index = name.indexOf(".");
				if (index != -1) {
					name = name.substring(0, index);
				}
			}

			List<Element> children = getChildren(rootElement);
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
			if (rv.size() > 0) {
				NcbcResource rsc = rv.get(0);
				name = rsc.getCenter();				
			}
			
		} catch (Exception e) {
			logger.warn("Parsing failed", e);
		}
		return rv;
	}

	public static Element resouce2Jdom(NcbcResource rsc) {
		rsc.simplifyOntology();
		Element ele = new Element(RESOURCE, NS_DESC);

		Attribute about = new Attribute("about", rsc.getName(), NS_RDF);
		ele.setAttribute(about);
		ele.addContent(textElement(PROP_CENTER, rsc.getCenter(), NS_DESC));
        
		for (String name : infomationModel) {
			String value = getProperty(rsc, name);
			if (value == null) {
				continue;
			}
			Element propE = new Element(name, NS_DESC);
			ele.addContent(propE);
			if (name.equals(PROP_TYPE)) {
				String[] strs = value.split(";");
				for (int n = 0; n < strs.length; n++) {
					String tmp = strs[n].trim();
					if (tmp.length() != 0) {
						Element ontology = new Element(tmp, NS_BRO);
						propE.addContent(ontology);
					}
				}
			} else {
				propE.setText(value);
			}
		}
		addVersion(ele, rsc);

		return ele;
	}

	private static void addVersion(Element ele, NcbcResource rsc) {
		Element vdsE = null;
		String value = rsc.getProperty(Descriptor.PROP_VDS);
		if (value != null && !value.equals(",,")) {
			vdsE = new Element(PROP_VERSION_INFORMATIONL, NS_DESC);

			String[] vds = value.split(",", 3);
			if (vds.length > 0 && vds[0].length() != 0) {
				Element v = new Element(PROP_VERSION, NS_DESC);
				v.setText(vds[0]);
				vdsE.addContent(v);
			}

			if (vds.length > 1 && vds[1].length() != 0) {
				Element v = new Element(PROP_RELEASE_DATE, NS_DESC);
				v.setText(vds[1]);
				vdsE.addContent(v);
			}

			if (vds.length > 2 && vds[2].length() != 0) {
				Element v = new Element(PROP_STAGE, NS_DESC);
				v.setText(vds[2]);
				vdsE.addContent(v);
			}

			Element propE = new Element(PROP_VERSION_INFORMATION, NS_DESC);
			ele.addContent(propE);
			propE.addContent(vdsE);
		}
	}

	public String getName() {
		return name;
	}
	public boolean isForSandBox() {
		return forSandBox;
	}

	public void setForSandBox(boolean forSandBox) {
		this.forSandBox = forSandBox;
	}

}
