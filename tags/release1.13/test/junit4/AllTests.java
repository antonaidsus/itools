package junit4;

import org.junit.runner.JUnitCore;

import parser.BiositeMapParserTest;
import parser.BiositeMapSearchHistoryParserTest;
import parser.CenterParserTest;
import parser.NcbcResourceParserTest;
import parser.RdfParserTest;

import biositemap.WebSearchTest;

public class AllTests {
	public static void main(String[] args) {
		// Result runClasses = JUnitCore.runClasses(
		// parser.RdfParserTest.class,
		// parser.NcbcResourceParserTest.class,
		// parser.BiositeMapParserTest.class,
		//		
		// biositemap.WebSearchTest.class,
		// biositemap.Test4Test.class);

		JUnitCore.main(new String[] { 
						RdfParserTest.class.getName(),
						NcbcResourceParserTest.class.getName(),
						BiositeMapParserTest.class.getName(),
						BiositeMapSearchHistoryParserTest.class.getName(),
						CenterParserTest.class.getName(),
						WebSearchTest.class.getName() });

	}
}
