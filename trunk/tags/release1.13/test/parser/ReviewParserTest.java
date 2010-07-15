package parser;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import edu.ucla.loni.ccb.itools.model.ResourceReview;
import edu.ucla.loni.ccb.itools.parser.ReviewParser;

public class ReviewParserTest {
	@Test()
	public void testParse() {
		File file = new File("./resources/testData/Test_Review.xml");
		List<ResourceReview> list;
		try {
			list = ReviewParser
					.parse(new FileInputStream(file));
			String str = ReviewParser.toXML(list);
			
			List<ResourceReview> list2 = ReviewParser.parse(new ByteArrayInputStream(str.getBytes()));
			String str2 = ReviewParser.toXML(list2);
			assertEquals(str, str2);
			System.out.println(str);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		}
	}

}
