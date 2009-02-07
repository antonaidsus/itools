package edu.ucla.loni.ccb.itools.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public abstract class JdomHelper {
	/**
	 * Builds a JDOM Document form the Reader
	 * 
	 * @author jeff.ma
	 * 
	 * @param input
	 *            A java.io.Reader
	 * 
	 * @return JDOM Document
	 * 
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Document buildDocument(Reader input) throws JDOMException,
			IOException {
		SAXBuilder builder = new SAXBuilder();

		return builder.build(input);
	}

	public static Element textElement(String tagName, String text) {
		Element v = new Element(tagName);
		v.setText(text);
		return v;
	}

	public static Element textElement(String tagName, String text, Namespace ns) {
		Element v = new Element(tagName, ns);
		v.setText(text);
		return v;
	}

	/**
	 * Gets the XML String of the JDOM Document
	 * 
	 * @param doc
	 *            JDOM Document
	 * @return the XML String of the JDOM Document
	 * @throws IOException
	 */
	public static String document2String(Document doc) throws IOException {
		StringWriter sw = new StringWriter();
		document2Writer(doc, sw);

		return sw.toString();
	}

	/**
	 * Gets the XML String of the JDOM Document
	 * 
	 * @param ele
	 *            JDOM Element
	 * @return the XML String of the JDOM Document
	 * @throws IOException
	 */
	public static String element2String(Element ele) {
		StringWriter sw = new StringWriter();
		try {
			element2Writer(ele, sw);
		} catch (IOException e) {
			return e.toString();
		}

		return sw.toString();
	}

	/**
	 * Writes the XML String of the Document to the Writer and close the Writer
	 * when done.
	 * 
	 * @param doc
	 *            JDOM Document
	 * @param writer
	 *            a java.io.Writer
	 * @throws IOException
	 */
	public static void document2Writer(Document doc, Writer writer)
			throws IOException {
		outp.output(doc, writer);
		writer.close();
	}

	/**
	 * Writes the XML String of the Document to the Writer and close the Writer
	 * when done.
	 * 
	 * @param ele
	 *            JDOM Element
	 * @param writer
	 *            a java.io.Writer
	 * @throws IOException
	 */
	public static void element2Writer(Element ele, Writer writer)
			throws IOException {
		outp.output(ele, writer);
		writer.close();
	}
	@SuppressWarnings("unchecked")
	public  static List<Element> getChildren(Element e) {
		return e.getChildren();
	}

	@SuppressWarnings("unchecked")
	public  static List<Attribute> getAttributes(Element e) {
		return e.getAttributes();
	}
	
	public static Attribute getAttribute(Element e, String name) {
		for (Attribute a : getAttributes(e) ) {
			if (a.getName().equals(name)) {
				return a;
			}
		}
		return null;
	}
	
	public static Element getChild(Element e, String childName) {
		for (Element e0 : getChildren(e)) {
			if (e0.getName().equals(childName)) {
				return e0;
			}
		}
		return null;
	}

	public String getName() {
		return "";
	}
	
	static XMLOutputter outp = new XMLOutputter();
	static {
		outp.setFormat(Format.getPrettyFormat());
	}
}
