package edu.ucla.loni.ccb.itools.servlet;

import java.io.IOException;

import org.acegisecurity.providers.encoding.ShaPasswordEncoder;
import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.RUser;

/**
 * Administers a user account for a server
 * 
 * @author Jeff Ma
 */

public class AccountAdminister {
	public static final Logger LOGGER = Logger.getLogger(AccountAdminister.class);
	//private static MessageDigest algorithm = null;
	private static RUser failedUser = new RUser();
	private static AccountAdminister INSTANCE = new AccountAdminister();
	private static ShaPasswordEncoder encoder = new ShaPasswordEncoder();

	static {
		failedUser.setRole(RUser.NOTTHING);
//		try {
//			algorithm = MessageDigest.getInstance("SHA-1");
//			
//		} catch (Exception e) {
//			LOGGER.warn("AccountAdminister init problem",e);
//		}
	}
	
	
	private MailService mailService;
	
	public static AccountAdminister getInstance() {
		return INSTANCE;
	}
	
	private AccountAdminister() {
		INSTANCE = this;
	}
	
	public MailService getMailService() {
		return mailService;
	}
	
	public void setMailService(MailService mservice) {
		mailService = mservice;
	}
	
	/**
	 * @return an Array of all user names.
	 */
	public String[] getAllUserNames() {
		return DaoFactory.getDaoFactory().getUserDao().getAllUserNames();
	}

	synchronized public RUser createAccount(String user, String pswd,
			String email, boolean request4Expert) throws IOException {
        
		RUser ruser = DaoFactory.getDaoFactory().getUserDao().getUser(user);

		if (ruser != null) {
			throw new IOException("account " + user + " already exists");
		}
		String passwd = encode(pswd);
		ruser = new RUser(user, passwd, email);
        if (user.equals("admin")) {
        	ruser.setRole(RUser.ADMINISTRATOR);
        } else if (request4Expert) {
			if (!"on".equals(Main.getProperty("account.security"))) {
			    ruser.setRole(RUser.EXPERTUSER);
			}
			if (mailService != null) {
				try {
					mailService.sendMail(ruser);
				} catch (RuntimeException e) {
					LOGGER.warn("send mail has problem:" + e);
				}
			}
		}
		DaoFactory.getDaoFactory().getUserDao().addUser(ruser);

		return ruser;
	}
	
	synchronized void setUserRole(String user, String newrole) throws IOException {
		RUser ruser = DaoFactory.getDaoFactory().getUserDao().getUser(user);
		if (ruser == null) {
			throw new IOException("account " + user + " does not exist.");
		}
		
		ruser.setRole(newrole);
		DaoFactory.getDaoFactory().getUserDao().updateUser(ruser);
	}

	/**
	 * Validates the user given the user name and password.
	 * 
	 * @param user
	 *            a <code> String </code> specifying the name of a user
	 * @param pwd
	 *            a <code> String </code> specifying the password of the user
	 * @return role of the user or -1 if on user or password did not match.
	 */
//	synchronized RUser validateUser(String user, String pswd) {
//		RUser ruser = DaoFactory.getDaoFactory().getUserDao().getUser(user);
//      
//		if (ruser == null) {
//			return failedUser;
//		}
//
//		if (ruser.getPasswd().equals(encode(pswd))) {
//			return ruser;
//		}
//		return failedUser;
//	}
//	
	/**
	 * Changes the user's password.
	 * 
	 * @param user
	 *            a <code> String </code> specifying the name of a user
	 * @param p1
	 *            a <code> String </code> specifying the old password
	 * @param p2
	 *            a <code> String </code> specifying the new password
	 * @return a <code> boolean </code> value specifying whether the user's
	 *         password has been changed
	 */
	synchronized boolean changePasswd(String user, String p1, String p2) {
		boolean rval = false;
		//RUser ruser = DaoFactory.getDaoFactory().getUserDao().getUser(user);
		try {
			//TODO
		} catch (Exception e) {
			return false;
		}
		return rval;
	}


	/**
	 * Encrypts byte array password into the String format
	 * 
	 * @param b
	 *            a <code> byte[] </code> specifying the password in a byte
	 *            array
	 * @return a <code> String </code> specifying the password ins string format
	 */
//	private static String bytes2String(byte[] b) {
//		algorithm.reset();
//		algorithm.update(b);
//		byte[] hash = algorithm.digest();
//
//		StringBuffer sbf = new StringBuffer();
//
//		for (int i = 0, I = hash.length; i < I; i++) {
//			int v = hash[i] & 0XFF;
//			sbf.append(Integer.toString(v, 16));
//		}
//
//		return sbf.toString();
//	}
	public static String encode(String password) {
		return encoder.encodePassword(password, null);
	}
}
