package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Test;

import edu.ucla.loni.ccb.itools.biositemap.BioSiteMapSearchHistory;
import edu.ucla.loni.ccb.itools.parser.BioSiteMapSearchHistoryParser;

public class BiositeMapSearchHistoryParserTest {
	@Test()
	public void testParse() {
		File file = new File("./resources/testData/BioSiteMapSearchHistory.xml");
		try {
			BioSiteMapSearchHistory history = BioSiteMapSearchHistoryParser
					.parse(new FileInputStream(file));
			String str = BioSiteMapSearchHistoryParser.toXML(history);
			
			BioSiteMapSearchHistory history2 = BioSiteMapSearchHistoryParser.parse(new ByteArrayInputStream(str.getBytes()));
			String str2 = BioSiteMapSearchHistoryParser.toXML(history2);
			assertEquals(str, str2);
			System.out.println(str);
			assertEquals(2, history.getBioSiteMapResearchRecords().size());
			assertEquals(4, history.getSummaries().size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		}
	}

}
