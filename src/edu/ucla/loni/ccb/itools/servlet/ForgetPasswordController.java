package edu.ucla.loni.ccb.itools.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.RUser;

public class ForgetPasswordController extends IToolsController {
	public static final Logger LOGGER = Logger.getLogger(ForgetPasswordController.class);

	public ModelAndView handleRequest(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		String username = arg0.getParameter("name").trim();
		String email = arg0.getParameter("email").trim();
		LOGGER.info("username:" + username + ", email:\"" + email + "\" forgetwassword");
		String replyMsg = string;
		String replyAddr= null;
		RUser user = null;
		if (username.length() != 0) {
			user = getUserByName(username);
			if (user != null) {
				String u_email = user.getEmail();
				if (u_email != null && u_email.trim().length() != 0) {
					replyMsg = string0;
					replyAddr = u_email;
				} else if (email.length() != 0) {
					replyMsg= string1 + email;
					replyAddr = email;
				} else {
					replyMsg= string2;
				}				
			}
		} else if (email.length() != 0) {
			user = getUserByEMail(email);
			if (user != null) {
				replyAddr = email;
			}
		}
		
		if (replyAddr != null) {
			String password = RandomStringUtils.randomAlphanumeric(10);
			user.setPasswd(AccountAdminister.encode(password));
			String emailMsg = "This is tempory password: please modify ASAP.\n" +
			"Username: " +  user.getName() +"\n" +
			"Password: " + password ;
			

			SimpleMailMessage message = new SimpleMailMessage();
			message.setText(emailMsg);
			message.setTo(replyAddr);
			message.setSubject("Temporally password of iTools account");
			AccountAdminister.getInstance().getMailService().sendMail(message);
			DaoFactory.getDaoFactory().getUserDao().updateUser(user);
		}
		return writeResponse(arg1, replyMsg);
	}
	
	private RUser getUserByName(String name) {
		return DaoFactory.getDaoFactory().getUserDao().getUser(name);
	}
	
	private RUser getUserByEMail(String email) {
		String[] allUserNames = DaoFactory.getDaoFactory().getUserDao().getAllUserNames();
		for (int i = 0; i < allUserNames.length; i++) {
			RUser u = getUserByName(allUserNames[i]);
			if (email.equals(u.getEmail())) {
				return u;
			}
		}
		return null;
	}
	
    public static final String string = "Password was not created because no account was found.";
	public static final String string0 = "Password was created and emailed to the email address " +
			"you provided when the account was created";
	public static final String string1 = "Password was created and emailed to the email address ";
	public static final String string2 = "Password was not created because no email address to send to.";
}
