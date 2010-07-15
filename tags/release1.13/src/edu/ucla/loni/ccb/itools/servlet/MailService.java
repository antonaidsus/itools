package edu.ucla.loni.ccb.itools.servlet;

import org.apache.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import edu.ucla.loni.ccb.itools.model.RUser;

/**
 * This class is responsible to send the administer an email when
 * a new user register and request an expert user or an expert user updated 
 * a center.
 * 
 * @author Jeff Qunfei Ma
 *
 */
public class MailService {
	public static final Logger logger = Logger.getLogger(MailService.class);
	
    private MailSender mailSender;
    private SimpleMailMessage newUserMailMessage;
    private SimpleMailMessage centerUpdateMailMessage;
    private SimpleMailMessage generalMailMessage;
    
	public SimpleMailMessage getCenterUpdateMailMessage() {
		return centerUpdateMailMessage;
	}
	public void setCenterUpdateMailMessage(SimpleMailMessage centerUpdateMailMessage) {
		this.centerUpdateMailMessage = centerUpdateMailMessage;
	}
	public SimpleMailMessage getNewUserMailMessage() {
		return newUserMailMessage;
	}
	public void setNewUserMailMessage(SimpleMailMessage mailMessage) {
		this.newUserMailMessage = mailMessage;
	}
	public MailSender getMailSender() {
		return mailSender;
	}
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	/**
	 * Sends the administrator that a new user registered and requested an Expert user.
	 * @param ruser the user's information.
	 */
	public void sendMail(RUser ruser) {
		SimpleMailMessage message = new SimpleMailMessage(this.newUserMailMessage);
		StringBuffer messageText = new StringBuffer(newUserMailMessage.getText());
		messageText.append("\n\nuser name:  " + ruser.getName());
		messageText.append("\nuser email: " + ruser.getEmail());
		message.setText(messageText.toString());
		sendMail(message);
	}
	
	public void sendMail(SimpleMailMessage message) {
		try {
			mailSender.send(message);
		} catch (MailException e) {
			logger.warn("failed send out email:" + message, e);
		}
	}
	
	/**
	 * Sends the administrator that the center is updated by the user.
	 * @param center
	 * @param ruser
	 */
	public void sendMail(String center, RUser ruser) {
		SimpleMailMessage message = new SimpleMailMessage(this.centerUpdateMailMessage);
		StringBuffer messageText = new StringBuffer(centerUpdateMailMessage.getText());
		messageText.append("\n\ncenter:  " + center);
		messageText.append("\n\nuser name:  " + ruser.getName());
		messageText.append("\nuser email: " + ruser.getEmail());
		message.setText(messageText.toString());
		sendMail(message);
	}
	
	/**
	 * Sends administrator the email with subject and text.
	 * @param subject
	 * @param text
	 */
	public void sendMail(String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage(this.generalMailMessage);
		message.setSubject(subject);
		message.setText(text);
		sendMail(message);
	}
	
	public void setGeneralMailMessage(SimpleMailMessage generalMailMessage) {
		this.generalMailMessage = generalMailMessage;
	}
	
	public SimpleMailMessage getGeneralMailMessage() {
		return generalMailMessage;
	}

}
