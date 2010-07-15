package edu.ucla.loni.ccb.itools.dao.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.CcbSoftwareClassificationExampleParser;
import edu.ucla.loni.ccb.itools.parser.IatrResourceParser;
import edu.ucla.loni.ccb.itools.parser.NcbcResourceParser;

/**
 * This class is real NcbcResource DAO for file system. It loads and save NCBC
 * Resource from and to a XML file. It will use its own XML file. All the
 * resources eventually should be defined in this format.
 * 
 * @author Qunfei Ma
 * 
 */
public class NcbcResourceIO {
	private static final String ONTOLOGY_FILENAME = "BiomedicalResourceOntology.xml";
	private static final Logger logger = Logger.getLogger(NcbcResourceIO.class);
	private static Map<String, List<NcbcResource>> loadedResources = new HashMap<String, List<NcbcResource>>();
	private static String resource_dir = "";
	private static String review_dir = "";
	private static File ontologyFile;
	private static boolean need_try_classpath = true;
    
	static {
		resource_dir = Main.getProperty("resource.dir");
		if (resource_dir == null || resource_dir.trim().length() == 0) {
			if (Main.isServerMode()) {
				resource_dir = Main.getProperty("server.resource.save.dir");
				need_try_classpath = false;
			} else {
				resource_dir = Main.getProperty("local.resource.save.dir");
			}
			Main.putProperty("resource.dir", resource_dir);
		} else {
			Main.putProperty("server.resource.save.dir", resource_dir);
			Main.putProperty("local.resource.save.dir", resource_dir);
			need_try_classpath = false;
		}
		review_dir = resource_dir + File.separator + "reviews";
		ontologyFile = new File(resource_dir + File.separator
				+ ONTOLOGY_FILENAME);

		File dir = new File(review_dir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		logger.info("resource.dir:" + resource_dir);
	}

	public static String getReviewSaveDir() {
		return review_dir;
	}

	public static String getResourceSaveDir() {
		return resource_dir;
	}

	public static String center2Filename(String center) {
		return center2Filename(center, false);
	}

	public static String center2Filename(String center, boolean forBackup) {
		NcbcCenter center2 = DaoFactory.getDaoFactory().getCenterDao().getCenter(center);
		String filename = "OtherExternalCenter";
		if (center2 != null) {
			filename = center2.getName();
		}
		if (forBackup) {
			filename += "_backup";
		}
		return resource_dir + File.separator + filename + ".xml";
	}

	public static void saveCenter(String center) {
		saveCenter(center, false);
	}

	public static void saveCenter(String center, boolean backup) {
		String filename = center2Filename(center, backup);
		List<NcbcResource> list = loadedResources.get(filename);
		if (list == null) {
			logger.error("No resources ever loaded for " + center);
		} else {
			write(list, filename);
			logger.info("saveCenter(String, boolean) to:" + filename + ", rsc#:" + list.size());
		}
	}

	public static List<NcbcResource> getResources(String center) {
		List<NcbcResource> list = new ArrayList<NcbcResource>(0);
		try {
			String filename = center2Filename(center);
			list = loadedResources.get(filename);
			if (list == null) {
				list = load(center);
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		return list;
	}

	static synchronized List<NcbcResource> load(String center)
			throws IOException {
		logger.info("start loading for center: " + center);
		List<NcbcResource> resources = null;
		String xmlfile = center2Filename(center);
		/*
		CenterDao centerDao = DaoFactory.getDaoFactory().getCenterDao();
		if (centerDao.getCenter(center) == null) {
			centerDao.addExternalCenter(center);
			resources = new ArrayList<NcbcResource>();
		} else*/ {
			resources = doLoad(center, xmlfile);
		}
		putResources(xmlfile, resources);
		logger.info(resources.size() + " were  loaded");
		return resources;
	}

	static List<NcbcResource> doLoad(String center, String xmlfile)
			throws FileNotFoundException {
		return doLoad(center, new File(xmlfile));
	}

	static List<NcbcResource> doLoad(String center, File file)
			throws FileNotFoundException {
		List<NcbcResource> resources;
		logger.debug(file.getAbsolutePath() + " exists: " + file.exists());
		if (!file.exists()) {
			if (need_try_classpath) {
				logger.info("starting load from classpath: " + center);
				resources = loadFromClassPath(center);
				for (Iterator<NcbcResource> iter = resources.iterator(); iter
						.hasNext();) {
					NcbcResource element = iter.next();
					element.setCenter(center);
				}
			} else {
				resources = new ArrayList<NcbcResource>(0);
			}
		} else {
			resources = NcbcResourceParser.parse(new FileInputStream(file));
		}
		return resources;
	}

	private static List<NcbcResource> loadFromClassPath(String center) {
		String str = "/" + center + ".xml";
		InputStream stream = NcbcResourceIO.class.getResourceAsStream(str);
		if (stream != null) {
			return NcbcResourceParser.parse(stream);
		}

		str = "/" + center + (center.equals("IATR") ? ".csv" : ".txt");
		stream = NcbcResourceIO.class.getResourceAsStream(str);
		if (stream == null) {
			logger.warn(str + " is not found from classpath.");
			return new ArrayList<NcbcResource>(0);
		}
		if (center.equals("IATR")) {
			return IatrResourceParser.parseCSV(stream);
		} else {
			return CcbSoftwareClassificationExampleParser.parseAsList(stream);
		}
	}

	public static void putResources(String filename, List<NcbcResource> rscs) {
		loadedResources.put(filename, rscs);
	}

	public static List<NcbcResource> getAllResources() {
		LinkedList<NcbcResource> rv = new LinkedList<NcbcResource>();
		for (NcbcCenter center : DaoFactory.getDaoFactory().getCenterDao()
				.getCenters()) {
			rv.addAll(getResources(center.getName()));
		}
		return rv;
	}

	/**
	 * Remove the Resource from memory and from disk. It will update to the
	 * database.
	 * 
	 * @param rsc
	 * @return
	 */
	public static void removeResource(NcbcResource rsc) {
		deletResourceFromMemory(rsc);
		saveCenter(rsc.getCenter());
	}

	private static Collection<NcbcResource> deletResourceFromMemory(
			NcbcResource rsc) {
		Collection<NcbcResource> list = getResources(rsc.getCenter());
		String name = rsc.getName();
		for (Iterator<NcbcResource> iter = list.iterator(); iter.hasNext();) {
			NcbcResource element = iter.next();
			if (element == null) {
				logger.warn(rsc.getCenter() + " has null element");
				iter.remove();
			} else if (element.getName().equals(name)
					&& element.isInSandBox() == rsc.isInSandBox()) {
				iter.remove();
				break;
			}
		}
		return list;
	}

	/**
	 * Save the Resource. It will update to the database.
	 * 
	 * @param rsc
	 * @return
	 */
	public static void saveResource(NcbcResource rsc) {
		Collection<NcbcResource> list = deletResourceFromMemory(rsc);
		list.add(rsc);
		saveCenter(rsc.getCenter());
	}

	private static void write(List<? extends NcbcResource> resources,
			String filename) {
		writeStr2File(NcbcResourceParser.getSaveString(resources), new File(
				filename));
	}

	public static void writeStr2File(String str, File file) {
		try {
			FileWriter output = new FileWriter(file);
			output.write(str);
			output.close();
		} catch (IOException e) {
			logger.warn(e, e);
		}
	}

	public static String readStrFromStream(InputStream input) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) != -1) {
				out.write(buf, 0, bytesRead);
			}
			out.close();
			input.close();
			return out.toString();
		} catch (Exception e) {
			return "read from input failed";
		}
	}

	public static void backUp(String center) {
		saveCenter(center, true);
	}

	public static List<NcbcResource> recover(String center) {
		List<NcbcResource> rv = new ArrayList<NcbcResource>(0);
		String xmlfile = center2Filename(center, true);
		File file = new File(xmlfile);
		if (file.exists()) {
			try {
				rv = NcbcResourceParser.parse(new FileInputStream(file));
			} catch (FileNotFoundException e) {
			}
		} else {
			logger.warn("No backup file, do nothing");
		}
		putResources(xmlfile, rv);
		return rv;

	}

	public static String getOntologyString() {
		InputStream input = null;
		try {
			if (ontologyFile.exists()) {
				input = new FileInputStream(ontologyFile);
			} else {
				input = NcbcResourceIO.class
						.getResourceAsStream("/" + ONTOLOGY_FILENAME);
			}
		} catch (FileNotFoundException e) {
			logger.warn("read Ontology error:", e);
		}

		String readStrFromStream = readStrFromStream(input);
//		LOGGER.debug("ontology string read:" + readStrFromStream);
		return readStrFromStream;
	}

	public static void setOntologyString(String ontology) {
		writeStr2File(ontology, ontologyFile);
	}

	public static void setResources(List<NcbcResource> rscs) {
		if (logger.isDebugEnabled()) {
			logger.debug("setResources(List<NcbcResource>) - start");
		}

		if (rscs.size() == 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("setResources(List<NcbcResource>) - end, Zero new rscs");
			}
			return;
		}
		
		NcbcResource object = rscs.get(0);
		String center = object.getCenter();
		backUp(center);
		putResources(NcbcResourceIO.center2Filename(center), rscs);
		saveCenter(center);		

		if (logger.isDebugEnabled()) {
			logger.debug("setResources(List<NcbcResource>) - end, resource number:" + rscs.size() );
		}
	}
}
