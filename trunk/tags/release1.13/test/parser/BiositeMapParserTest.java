package parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.BioSiteMapParser;
import edu.ucla.loni.ccb.itools.parser.CcbSoftwareClassificationExampleParser;

public class BiositeMapParserTest extends TestCase {
	public void testParse() {
		File file = new File("./resources/testData/test-biositemap.xml");
		BioSiteMapParser parser = new BioSiteMapParser();
		try {
			List<NcbcResource> list = parser.parse(new FileInputStream(file));
			assertEquals(3, list.size());
			String str = BioSiteMapParser.getSaveString(list);
			
			BioSiteMapParser parser2 = new BioSiteMapParser();
			List<NcbcResource> list2 = parser2.parse(new ByteArrayInputStream(str.getBytes()));

			String str2 = BioSiteMapParser.getSaveString(list2);

			assertEquals(str, str2);
			System.out.println(str);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
