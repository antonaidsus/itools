package edu.ucla.loni.ccb.itools.parser;

import org.apache.log4j.Logger;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class MyXmlParser extends DefaultHandler {
	public static final String S4="    ";
	public static final String S8="        ";

    public static final Logger LOGGER = Logger.getLogger(MyXmlParser.class);
    /** Used to report line number. */
    private Locator mLocator;
	/** Logger for this class. */

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator locator) {
        this.mLocator = locator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException e) {
        _reportSAXParseException(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException e) {
        _reportSAXParseException(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException e) {
        _reportSAXParseException(e);
    }

    /**
     * @param e
     */
    private void _reportSAXParseException(SAXParseException e) {
        if (mLocator != null) {
            LOGGER.error("In " + mLocator.getSystemId() + ", at line "
                    + mLocator.getLineNumber() + ", and column "
                    + mLocator.getColumnNumber(), e);

        }
    }
    
    protected static boolean s2b(String s) {
    	return Boolean.valueOf(s).booleanValue();
    }
    
    public static String strOfTag(String name, String value) {
    	return startTag(name) + value + endTag(name);
    }
    
    public static String startTag(String tag) {
    	return 	"<" + tag + ">";
    }
    
    public static String endTag(String tag){
    	return "</" + tag + ">\n";
    }
    

}