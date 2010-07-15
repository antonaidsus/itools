package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.hsqldb.lib.StringInputStream;
import org.junit.Test;

import edu.ucla.loni.ccb.itools.Util;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.NcbcResourceParser;

public class NcbcResourceParserTest {
	@SuppressWarnings("unchecked")
	@Test()
	public void testMain() {
		File sample = new File("resources/testData/ccb.xml");
		try {
			// get the list of NcbcResouces
			FileInputStream input = new FileInputStream(sample);
			System.out.println(input.available());
			List list = NcbcResourceParser.parse(input);
			assertEquals(34, list.size());

//			String str = NcbcResourceParser.getSaveString(list);
//			System.out.println("parser sucessed parse the string");
//			// CCB Resource format string 
//			System.out.println(str);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	@Test()
	public void parseSpecialCharacter() {
		String fileName = "resources/testData/ncbcResource_specialCharacter.xml";
		try {
			FileInputStream input = new FileInputStream(fileName);			
			List<NcbcResource> list = NcbcResourceParser.parse(input);
			
			assertEquals(1, list.size());
			
			String str = NcbcResourceParser.getSaveString(list);
			
			List<NcbcResource> list2 = NcbcResourceParser.parse(new ByteArrayInputStream(str.getBytes()));
			String str2 = NcbcResourceParser.getSaveString(list2);
			System.out.println(str2);
			NcbcResource rsc = list.get(0);
			assertEquals("jViewbox & xViewFlut", rsc.getName());
			assertEquals("medical imaging research & study", rsc.getProperty("Description"));
			assertEquals(str, str2);
		} catch (Exception e) {
			e.printStackTrace();
			fail();

		}
	}
}
