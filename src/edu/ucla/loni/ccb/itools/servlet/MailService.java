package edu.ucla.loni.ccb.itools.servlet;

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
    private MailSender mailSender;
    private SimpleMailMessage newUserMailMessage;
    private SimpleMailMessage centerUpdateMailMessage;
    
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
		mailSender.send(message);
	}
	
	public void sendMail(SimpleMailMessage message) {
		mailSender.send(message);
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
		mailSender.send(message);
	}
}
