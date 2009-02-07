package edu.ucla.loni.ccb.itools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;

import edu.ucla.loni.ccb.itools.model.ResourceReview;

public class ReviewParser extends MyXmlParser {
	private static final String AUTHOR = "Author";
	private static final Object CREATEDDATE = "createdDate";
	private static final Object TITLE = "title";
	private static final Object CONTENT = "comment";
	private static final Object RATE = "rate";
	private static String REVIEWS = "reviews";
	private static String REVIEW = "review";
	private static String VOTE = "vote";
	
	private StringBuffer currentBuffer;
	private List<ResourceReview> resourceReviews = new ArrayList<ResourceReview>();
	private ResourceReview resourceComment;

	
	public static List<ResourceReview> parse(InputStream input) {
		ReviewParser container = new ReviewParser();

		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(input, container);
		} catch (Exception e) {
			LOGGER.warn(e, e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				LOGGER.warn(e, e);
			}
		}
		return container.resourceReviews;

	}
    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
            Attributes atts) {
    	currentBuffer = new StringBuffer();
        if(qName.equals(REVIEW)) {
        	resourceComment = new ResourceReview();
        	for (int i = 0, I = atts.getLength(); i < I; i++) {
                String a = atts.getQName(i);
                if (a.equals("resourceName")) {
        	        resourceComment.setResourceName(atts.getValue(i));
                } else if (a.equals("id")) {
        	        resourceComment.setId(Integer.parseInt(atts.getValue(i)));
                }
        	}
        	resourceReviews.add(resourceComment);
        } else if (qName.equals("vote")) {
        	for (int i = 0, I = atts.getLength(); i < I; i++) {
                String a = atts.getQName(i);
                int v = Integer.parseInt(atts.getValue(i));
                if (a.equalsIgnoreCase("total")) {
                	resourceComment.setTotalvote(v);               	
                } else if (a.equalsIgnoreCase("yes")) {
                	resourceComment.setYesvote(v);
                }
            }
        }
    }
    
    public void endElement(String uri, String localName, String qName) {
    	String str = currentBuffer.toString().trim();
		if (qName.equals(AUTHOR)) {
        	resourceComment.setAuthor(str);
        } else if (qName.equals(CREATEDDATE)) {
        	resourceComment.setCreatedDateAsString(str);
        } else if (qName.equals(TITLE)) {
        	resourceComment.setTitle(str);
        } else if (qName.equals(CONTENT)) {
        	resourceComment.setComment(str);
        } else if (qName.equals(RATE)) {        	
        	resourceComment.setRate(Integer.parseInt(str));
        } 
    }
    
    public void characters(char[] ch, int start, int length) {
        currentBuffer.append(ch, start, length);
    }
    
    public static String toXML(List<ResourceReview> reviews) {
    	StringBuffer buff = new StringBuffer();
    	buff.append("<" + REVIEWS + ">\n");
    	for (ResourceReview el :  reviews) {
			buff.append(toXML(el));
		}
    	buff.append("</" + REVIEWS + ">\n");
    	return buff.toString();   	
    }

	public static String toXML(ResourceReview el) {
		String str =
			S4 + "<" +REVIEW + " resourceName=\"" + el.getResourceName() + 
			    "\" id =\"" + el.getId() + "\">\n" +
		    S8 + "<" + AUTHOR + ">" + el.getAuthor() + "</" +AUTHOR + ">\n" +
		    S8 + "<" + CREATEDDATE + ">" + el.getCreatedDateAsString() + "</" + CREATEDDATE + ">\n" +
		    S8 + "<" + RATE + ">" + el.getRate() + "</" + RATE + ">\n" +
		    S8 + "<" + TITLE + ">" + el.getTitle() + "</" + TITLE + ">\n" +
		    S8 + "<" + VOTE + " total=\"" + el.getTotalvote() + "\" yes=\"" + el.getYesvote() + "\"/>\n" +
		    S8 + "<" + CONTENT + ">" + el.getComment() + "</" + CONTENT + ">\n" +
		    S4 + "</" +REVIEW + ">\n";
		    
		return str;
	}
	public static String getSaveString(ResourceReview value, String string) {
		return toXML(value);
	}

}
