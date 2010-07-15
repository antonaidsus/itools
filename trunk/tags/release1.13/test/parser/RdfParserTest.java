package parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Namespace;
import org.junit.Test;

import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.NcbcResourceParser;
import edu.ucla.loni.ccb.itools.parser.RdfParser;

public class RdfParserTest {
	@Test()
	public void testParse() {
		try {
			File sample = new File("resources/testData/sample.rdf");
			RdfParser rdfParser = new RdfParser();
			List<?> list = rdfParser.parse(new FileInputStream(sample));
			assertEquals(2, list.size());
			sample = new File("resources/testData/ncbo-test.rdf");
			list = rdfParser.parse(new FileInputStream(sample));
			assertEquals(23, list.size());
			list = rdfParser.parse(new FileInputStream(
					"resources/testData/Magnet-test.rdf"));
			assertEquals(19, list.size());
			list = rdfParser.parse(new FileInputStream(
					"resources/testData/simbios-test.rdf"));
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
	}

	@Test()
	public void testParse1() {
		try {
			InputStream input = new URL(
					"http://www.ncbcs.org/biositemaps/I2B2_biositemap.rdf")
					.openStream();
			RdfParser rdfParser = new RdfParser();
			List<NcbcResource> list = rdfParser.parse(input);
			assertEquals("I2B2", list.get(0).getCenter());
		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Test()
	public void testMain() {
		File sample = new File("resources/testData/ncbo-test.rdf");
		try {
			// get the list of NcbcResouces
			RdfParser rdfParser = new RdfParser();
			List list = rdfParser.parse(new FileInputStream(sample));

			String str = NcbcResourceParser.getSaveString(list);
			// CCB Resource format string
			System.out.println(str);
			// Save to RDF format
			System.out.println("\n\n" + RdfParser.getSaveString(list));
			//
			Document rdf = RdfParser.buildDocument(new FileReader(sample));
			System.out.println(rdf.getRootElement().getName());

			System.out
					.println("this following string is without transfrom NcbcResoures");
			System.out.println(RdfParser.document2String(rdf));
			List<Attribute> attributes = rdf.getRootElement().getAttributes();
			for (Attribute a : attributes) {
				System.out.println(a);
				System.out.println(a.getName());
				System.out.println(a.getQualifiedName());
				System.out.println(a.getNamespacePrefix());
				System.out.println(a.getNamespaceURI());
				System.out.println(a.getValue());

				System.out.println(rdf.getRootElement().getAttribute(
						a.getQualifiedName()));
				System.out.println(rdf.getRootElement().getAttributeValue(
						a.getName()));

			}
			System.out.println(rdf.getRootElement().getAttributeValue("base",
					Namespace.XML_NAMESPACE));

			System.out.println(rdf.getRootElement().getAttributes().size());
			System.out.println(rdfParser.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
}
