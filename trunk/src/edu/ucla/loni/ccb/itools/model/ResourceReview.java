package edu.ucla.loni.ccb.itools.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import edu.ucla.loni.ccb.itools.Main;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * This class represents one Review on a NcbcResource.
 * @author Jeff Qunfei Ma
 * @hibernate.class
 */
public class ResourceReview {	
	public static final DateFormat DF = DateFormat.getDateInstance(DateFormat.MEDIUM,Locale.US);
	
	private String mResourceName;
    private String mTitle;
    private String mAuthor;
    private String mCreatedDateStr;
    private Date mCreatedDate;
    private String mComment;
    private int mRate;     //possible values are 2, 4, 6, 8, 10
	private int mTotalvote;
	private int mYesvote;
	
	private int id; //required by Hibernate.

    /**
     * @hibernate.id column="id" generator-class="native" unsaved-value="null"
     */
    public int getId() {
        return id;
    }

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @hibernate.property 
	 * @return the Authour who wrote this review
	 */
	public String getAuthor() {
		return mAuthor;
	}

	/**
	 * @hibernate.property 
	 * @return the comment part of this review, usually are multilines.
	 */
	public String getComment() {
		return mComment;
	}
	
	/**
	 * @hibernate.property 
	 * @return an int which is the user's rate, possible values are 2,4,6,8,10
	 */
	public int getRate() {
		return mRate;
	}
	/**
	 * @hibernate.property 
	 * @return the title part of this review
	 */
	public String getTitle() {
		return mTitle;
	}

	public Date getCreatedDate() {
		if (mCreatedDate == null) {
			try {
				mCreatedDate = DF.parse(mCreatedDateStr);
			} catch (ParseException e) {
				mCreatedDate = new Date();
				Main.LOGGER.warn(e, e);
			}
		}
		return mCreatedDate;
	}
	
	public void setCreatedDate(Date createdDate) {
		mCreatedDate = createdDate;
		mCreatedDateStr = DF.format(mCreatedDate);
	}
	
	/**
	 * @hibernate.property column="createdDate"
	 * @return a string represent the created Date
	 */
	public String getCreatedDateAsString() {
		return mCreatedDateStr;
	}
	
	public void setCreatedDateAsString(String createdDate) {
		mCreatedDateStr = createdDate;
	}
	
	/**
	 * @hibernate.property
	 * @return an int which is the total number user votes about this review.
	 */
	public int getTotalvote() {
		return mTotalvote;
	}

	public void setTotalvote(int totalvote) {
		mTotalvote = totalvote;
	}

	public String getVote() {
		return mYesvote + " of " + mTotalvote;
	}

	/**
	 * @hibernate.property
	 * @return the number which user votes "yes" to this review.
	 */
	public int getYesvote() {
		return mYesvote;
	}
	
	/**
	 * @hibernate.property column="resource_name" not-null="true"
	 * @return the name the resource for which this review is about.
	 */
	public String getResourceName() {
		return mResourceName;
	}

	public void setResourceName(String targetName) {
		mResourceName = targetName;
	}


	public void setYesvote(int yesvote) {
		mYesvote = yesvote;
	}

	public void setAuthor(String author) {
		mAuthor = author;
	}

	public void setComment(String comment) {
		mComment = comment;
	}

	public void setRate(int rate) {
		mRate = rate;
	}

	public void setTitle(String title) {
		mTitle = title;
	}
	
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof ResourceReview)) {
			return false;
		}
		ResourceReview rhs = (ResourceReview) object;
		return new EqualsBuilder().append(this.mResourceName, rhs.mResourceName)
				.append(this.mCreatedDateStr, rhs.mCreatedDateStr).append(this.mAuthor,
						rhs.mAuthor)
				.append(this.mRate, rhs.mRate).append(this.mTitle, rhs.mTitle).isEquals();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(1331924049, -717213321).append(
				this.mResourceName).append(this.mCreatedDateStr).append(this.mAuthor).append(this.mRate).append(this.mTitle)
				.toHashCode();
	}

}
