package parser;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import edu.ucla.loni.ccb.itools.Util;
import edu.ucla.loni.ccb.itools.model.NcbcCenter;
import edu.ucla.loni.ccb.itools.parser.CentersParser;

public class CenterParserTest {
  @Test()
  public void parseAndSave() {
	  String nameStr ="University of Illinois at Chicago Center for Clinical & Translational Science";
	  InputStream input = CenterParserTest.class.getResourceAsStream("centers.xml");
	  ByteArrayOutputStream out = new ByteArrayOutputStream();
	  try {
		Util.echoData(out, input);
		input.close();
		String expected = out.toString();
		input = CenterParserTest.class.getResourceAsStream("centers.xml");
		List<NcbcCenter> centers = CentersParser.parse(input);
		assertEquals(2, centers.size());
		assertEquals(nameStr, centers.get(1).getName());
//		System.out.println(centers.get(1).getName());
		String actual = CentersParser.toXml(centers);
//		System.out.println(CentersParser.getSaveString(centers.get(1)));
//		System.out.println("===EXPECTED====");
//		System.out.println(expected);
//		System.out.println("===ACTUAL====");
//		System.out.println(actual);
		assertEquals(expected, actual);
	} catch (IOException e) {
		e.printStackTrace();
		fail("parseAndSave()");
	}
  }
}
