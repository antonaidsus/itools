package edu.ucla.loni.ccb.itools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.csvreader.CsvReader;

import edu.ucla.loni.ccb.itools.model.NcbcResource;

/**
 * 
 * This class parse a CSV format.
 * @author Jeff Qunfei Ma
 *
 */
public class CSVParser {
	public static final String CSV_CREATOR = "Creator";
	public static final String CSV_PLATFORM = "Platforms";
	public static final String CSV_STATUS = "Status";
	public final static String CSV_VERSION = "Version";
	public final static String CSV_DATE = "Date";
	public final static String CSV_WWW = "WWW";
	
	public static List<NcbcResource> parse(InputStream input) {
		return parse(new InputStreamReader(input));
	}
	
	/**
	 * @param reader
	 * @return a List whose element is NcbcResource.
	 */
	public static List<NcbcResource> parse(Reader reader) {
		List<NcbcResource> rv = new LinkedList<NcbcResource>();
		CsvReader csvreader = new CsvReader(reader);

		try {			
			csvreader.readHeaders();
			String headers[] = csvreader.getHeaders();
			//NcbcTableSorter.addFieldNames(headers);
//The following is commented out. the IATR table is not important, I assume.
//			for (int i = 0; i < headers.length; i++) {
//				if (!(headers[i].equals(CSV_VERSION) ||
//						headers[i].equals(CSV_DATE) ||	
//						headers[i].equals(CSV_STATUS)||
//						headers[i].equals(CSV_PLATFORM)||
//						headers[i].equals(CSV_WWW)||
//						headers[i].equals(CSV_CREATOR))) {
//				    DescriptorParser.addFieldName(headers[i]);
//				}
//			}

			while (csvreader.readRecord()) {
				NcbcResource r = new NcbcResource();
				for ( int i = 0; i < headers.length; i++) {
					r.putProperty(headers[i], csvreader.get(i));
					//System.out.println(headers[i] +"=" + csvreader.get(i));
				}
				ResourceStandardizer.standardize(r);
				rv.add(r);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return rv;
	}

}
