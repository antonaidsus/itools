package edu.ucla.loni.ccb.itools.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import edu.ucla.loni.ccb.itools.Descriptor;

/**
 * This class represents one item of NCBC Resource. It could use hibernate to save to
 * database, and use xdoclet to create the Hibernate mapping file.
 * 
 * It can also be saved to a local file system, to improve the performance
 * different centers have its own file. so it has a field named "center".
 * 
 * @author <a href="mailto:qma66@hotmail.com">Jeff Ma</a>
 * @hibernate.class
 */
public class NcbcResource implements Serializable {
	public static final String SANDBOX = "sandbox";
	private int id;

	private String center;

	private Map<String, String> properties = new HashMap<String, String>();

	/**
	 * @hibernate.id column="id" generator-class="native" unsaved-value="null"
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @hibernate.property
	 * @return
	 */
	public String getCenter() {
		return center;
	}

	public void setCenter(String center) {
		this.center = center;
	}

	public void setInSandBox(boolean b) {
		if (b) {
			properties.put(SANDBOX, "true");
		} else {
			properties.remove(SANDBOX);
		}
	}

	public boolean isInSandBox() {
		return properties.get(SANDBOX) != null;
	}

	/**
	 * @hibernate.map cascade="all"
	 * @hibernate.collection-key column="resource_id"
	 * @hibernate.collection-index column="descriptor_name" type="string"
	 *                             length="50"
	 * @hibernate.collection-element column="descriptor_value" type="string"
	 *                               length="5000"
	 * @return all descriptors as a Map.
	 */
	public Map<String, String> getAllProperties() {
		return properties;
	}

	/**
	 * just required by Hibernate
	 * 
	 * @param props
	 */
	public void setAllProperties(Map<String, String> props) {
		this.properties = props;
	}

	/**
	 * I don't know what its item and its format, so now I use a Properties to
	 * keep items.
	 * 
	 * @param name
	 * @return
	 */
	public String getProperty(String name) {
		return (String) properties.get(name);
	}

	public String getOntologyAsString() {
		String ontology = getProperty(Descriptor.PROP_ONTOLOGY);
		if (ontology == null) {
			ontology = getProperty("Ontology");
		}
			
		if (ontology == null) {
			ontology = Descriptor.NOVALUE;
		} else {
			ontology = ontology.replaceAll("-->", Descriptor.DELIMITER);
			ontology = ontology.replaceAll("->", Descriptor.DELIMITER);
			ontology = ontology.replaceAll("-", Descriptor.DELIMITER);
			putProperty(Descriptor.PROP_ONTOLOGY, ontology);
		}
		return ontology;
	}

	public void simplifyOntology() {
		String ontology = getOntologyAsString();
		String[] strs = ontology.split(Descriptor.SEPARATOR);
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			int index = strs[i].lastIndexOf(Descriptor.DELIMITER);
			if (index != -1) {
				strs[i] = strs[i].substring(index + 1).trim();
			}
			if (i != 0) {
				buff.append(";");
			}
			buff.append(strs[i]);

		}
		putProperty(Descriptor.PROP_ONTOLOGY, buff.toString());
	}

	/**
	 * @return array of array of string, which is array of Ontology.
	 */
	public String[] getOntology() {
		String[] tologies = getOntologyAsString().split(Descriptor.SEPARATOR);
		String[] rv = new String[tologies.length];
		for (int i = 0; i < tologies.length; i++) {
			int index = tologies[i].lastIndexOf(Descriptor.DELIMITER);
			rv[i] = tologies[i].substring(index+1);
		}
		return rv;
	}

	public void putProperty(String name, String value) {
		properties.put(name, value);
	}

	/**
	 * returns a n Array of String whose length is the same as all the fields.
	 * 
	 * @return Array of String which will be added to to the table.
	 */
	public String[] getValues() {
		String[] names = Descriptor.getAllFieldNames();
		String[] values = new String[names.length];
		for (int i = 0; i < names.length; i++) {
			values[i] = (String) properties.get(names[i]);
			if (values[i] == null)
				values[i] = "";
		}
		return values;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return getProperty(Descriptor.PROP_NAME);
	}

	public boolean isValid() {
		return getName() != null;
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + getValues().hashCode();
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NcbcResource other = (NcbcResource) obj;
		return ArrayUtils.isEquals(getValues(), other.getValues());
	}

}
