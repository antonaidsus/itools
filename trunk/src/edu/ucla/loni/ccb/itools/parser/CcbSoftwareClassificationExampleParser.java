package edu.ucla.loni.ccb.itools.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlparser.beans.StringBean;

import edu.ucla.loni.ccb.itools.Descriptor;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.view.ResourceIndexTree;

/**
 * This class reads and parse the file formatted as Property : Value.
 * 
 * It reads file from classpath or directly from http address.
 * 
 * @author Jeff Qunfei Ma
 *
 */
public class CcbSoftwareClassificationExampleParser {
	private static StringBuffer possibleError;
	public static final String TITLE = "Software Classification & Metadata Examples";
	public static final String ERROR_STR="It may have a typo for ':', or it should be part of the value of ";
	public static final Logger logger = Logger
	.getLogger(CcbSoftwareClassificationExampleParser.class);
	
	public static String getPossibleError() {
		return possibleError.toString();
	} 
	
	public static NcbcResource[] parse(Reader reader) {
		List<NcbcResource> rv = parseAsList(reader);
		return (NcbcResource[]) rv.toArray(new NcbcResource[0]);
	}
	
	public static NcbcResource[] parse(InputStream input) {
		return parse(new InputStreamReader(input));
	}
	
	public static List<NcbcResource> parseAsList(InputStream input) {
		return parseAsList(new InputStreamReader(input));
	}
	
	/**
	 * From the HTTP URL htmlsrc, gets and parse the string
     * and returns  an Array of Resources.
	 * 
	 * @param htmlsrc the http address.
	 * @return an Array of Resources.
	 */
	public static NcbcResource[] parse(String htmlsrc) {
		List<NcbcResource> rv = parseAsList(htmlsrc);
		return (NcbcResource[]) rv.toArray(new NcbcResource[0]);
	}
	
	/**
	 * From the HTTP URL htmlsrc, gets and parse the string
     * and returns  a List of Resources.
	 * 
	 * @param htmlsrc the http address.
	 * @return a List of Resources.
	 */
	public static List<NcbcResource> parseAsList(String htmlsrc) {
		StringBean sb = new StringBean ();
        sb.setLinks (false);
        sb.setURL (htmlsrc);
        String src = sb.getStrings ();

        return doParse0(new StringReader(src));
	}

	private static String BEG = "[edit]";
	private static List<NcbcResource> doParse0(Reader reader) {
		possibleError = new StringBuffer();
		boolean realMeat = false; // resource definition not started yet.
		int singleNum = 0;   //if more than 5 lines which is not resource related, quit.
		List<NcbcResource> rv = new LinkedList<NcbcResource>();
		BufferedReader br = new BufferedReader(reader);
		String line = null;
		String questionableLine = null;
		String lastDescriptor = null;
		try {
			NcbcResource r = null;
			while((line=br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				if (line.startsWith(BEG)) {
					realMeat = true;
					line=br.readLine();
					if (line.indexOf(TITLE) == -1) { //it is a name
						if (r != null) {
							r.simplifyOntology();
						}
					    r = new NcbcResource();
					    rv.add(r);
					    r.putProperty(Descriptor.PROP_NAME, 
							line.trim());
					}
				} else {
					if (!realMeat) continue;
				    String[] pair = line.split(":", 2);
				    if (pair.length < 2 || (pair[0].indexOf("\"") != -1)) {
				    	questionableLine = line;
				    	logger.warn("***********" +line +"*************");
				    	if (singleNum++ > 4) break;		    	
				    } else { 
				    	r.putProperty(pair[0].trim(), pair[1]);	
				    	if (questionableLine != null) {
				    		possibleError.append(questionableLine + "\n\n" + ERROR_STR + "'" + lastDescriptor + "'\n");
				    		questionableLine = null;
				    	}
				    	lastDescriptor = pair[0];
				    }
				}
			}	
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
		
		for (NcbcResource rsc : rv) {
			rsc.simplifyOntology();
		}

		return rv;
	}
	
	//the method is used to parse static file
	public static List<NcbcResource> parseAsList(Reader reader) {
//		if (true) {
//			return doParse0(reader);
//		}
		possibleError = new StringBuffer();
		List<NcbcResource> rv = new LinkedList<NcbcResource>();
		BufferedReader br = new BufferedReader(reader);
		String line = null;
		try {
			NcbcResource r = null;
			while((line=br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0 || line.startsWith("#")) {
					continue;
				}
				if (line.startsWith(BEG)) {
					r = new NcbcResource();
					rv.add(r);
					r.putProperty(Descriptor.PROP_NAME, 
							line.substring(BEG.length()).trim());
				} else {
				    String[] pair = line.split(":", 2);
				    if (pair.length < 2 || (pair[0].indexOf("\"") != -1)) {
				    	logger.warn("***********" +line +"*************");
				    } else {
				    	r.putProperty(pair[0].trim(), pair[1]);				        
				    }
				}
			}	
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
		for (NcbcResource rsc : rv) {
			rsc.simplifyOntology();
		}
		return rv;
	}
	
	public static void main(String[] args) {
		String urls[] = {
				"http://na-mic.org/Wiki/index.php/SDIWG:NCBC_Software_Classification_NAMIC_Examples",
				"http://na-mic.org/Wiki/index.php/SDIWG:NCBC_Software_Classification_Simbios_Examples",
				"http://na-mic.org/Wiki/index.php/SDIWG:NCBC_Software_Classification_CCB_Examples",
				"http://na-mic.org/Wiki/index.php/SDIWG:NCBC_Software_Classification_I2B2_Examples",
				"http://na-mic.org/Wiki/index.php/SDIWG:NCBC_Software_Classification_NCIBI_Examples",
				"http://na-mic.org/Wiki/index.php/SDIWG:NCBC_Software_Classification_MAGNet_Examples",
				"http://na-mic.org/Wiki/index.php/SDIWG:NCBC_Software_Classification_NCBO_Examples",
		};
		List<NcbcResource> total = new ArrayList<NcbcResource>();
		StringBuffer errorStr = new StringBuffer();
		for (int i = 0; i < urls.length; i++) {
			List<NcbcResource> list = parseAsList(urls[i]);
			String error = getPossibleError();
			if (!(error == null || error.trim().length() == 0)) {
				errorStr.append(urls[i] + " may have error:\n\n" + error);
			}
			total.addAll(list);
		}
		System.out.println(NcbcResourceParser.getSaveString(total));
		try {Thread.sleep(1000);}catch(Exception e) {};
		System.err.println("Questionable lines are:\n"+ errorStr);
	}		
}
