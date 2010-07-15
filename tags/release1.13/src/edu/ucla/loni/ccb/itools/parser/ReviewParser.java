package edu.ucla.loni.ccb.itools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import edu.ucla.loni.ccb.itools.model.ResourceReview;

public class ReviewParser extends JdomHelper {
	private static final String YES = "yes";
	private static final String TOTAL = "total";
	private static final String ID = "id";
	private static final String RESOURCE_NAME = "resourceName";
	private static final String AUTHOR = "Author";
	private static final String CREATEDDATE = "createdDate";
	private static final String TITLE = "title";
	private static final String CONTENT = "comment";
	private static final String RATE = "rate";
	private static final String REVIEWS = "reviews";
	private static final String REVIEW = "review";
	private static final String VOTE = "vote";
	
	private List<ResourceReview> resourceReviews = new ArrayList<ResourceReview>();
	private static final Logger logger = Logger.getLogger(ReviewParser.class);
	
	public static List<ResourceReview> parse(InputStream input) {
		ReviewParser container = new ReviewParser();

		try {
			Document document = buildDocument(new InputStreamReader(input));
			Element rootElement = document.getRootElement();
			for (Element ele : getChildren(rootElement)) {
				container.resourceReviews.add( jdom2Review(ele));
			}	
		} catch (Exception e) {
			logger.warn(e, e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				logger.warn(e, e);
			}
		}

		return container.resourceReviews;

	}
	
    private static ResourceReview jdom2Review(Element ele) {
    	ResourceReview review = new ResourceReview();
    	review.setResourceName(ele.getAttributeValue(RESOURCE_NAME));
    	review.setId(Integer.parseInt(ele.getAttributeValue(ID)));
    	
    	for (Element child : getChildren(ele)) {
    		String qName = child.getName();
    		if (qName.equals(VOTE)) {
    			review.setTotalvote(Integer.parseInt(child.getAttributeValue(TOTAL)));
    			review.setYesvote(Integer.parseInt(child.getAttributeValue(YES)));
    		} else if (qName.equals(AUTHOR)) {
            	review.setAuthor(child.getText());
            } else if (qName.equals(CREATEDDATE)) {
            	review.setCreatedDateAsString(child.getText());
            } else if (qName.equals(TITLE)) {
            	review.setTitle(child.getText());
            } else if (qName.equals(CONTENT)) {
            	review.setComment(child.getText());
            } else if (qName.equals(RATE)) {        	
            	review.setRate(Integer.parseInt(child.getText()));
            } 
    	}
    	return review;
	}

    public static String toXML(List<ResourceReview> reviews) {
    	Element root = new Element(REVIEWS);
    	for (ResourceReview el :  reviews) {
			root.addContent(review2Jdom(el));
		}
    	return element2String(root); 	
    }

	public static Element review2Jdom(ResourceReview el) {
		Element rv = new Element(REVIEW);
		rv.setAttribute(RESOURCE_NAME, el.getResourceName());
		rv.setAttribute(ID, el.getId() + "");

		rv.addContent(textElement(AUTHOR,el.getAuthor()));
		rv.addContent(textElement(CREATEDDATE, el.getCreatedDateAsString()));
		rv.addContent(textElement(RATE, el.getRate()+""));
		rv.addContent(textElement(TITLE, el.getTitle()));
		Element vote = new Element(VOTE);
		vote.setAttribute(TOTAL, el.getTotalvote()+"");
		vote.setAttribute(YES, el.getYesvote()+"");
		rv.addContent(vote);
		rv.addContent(textElement(CREATEDDATE, el.getCreatedDateAsString()));
		rv.addContent(textElement(CONTENT, el.getComment()));
		
		return rv;
	}
	
	public static String getSaveString(ResourceReview value) {
		return element2String(review2Jdom(value));
	}

}
