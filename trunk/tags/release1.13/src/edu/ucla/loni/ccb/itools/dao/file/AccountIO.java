package edu.ucla.loni.ccb.itools.dao.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.Util;
import edu.ucla.loni.ccb.itools.model.RUser;
import edu.ucla.loni.ccb.itools.parser.AccountParser;

/**
 * This class write or read user account to or read from File System. The file
 * can be specified from property file.
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class AccountIO {
	static final Logger logger = Logger.getLogger(AccountIO.class);

	static private Map<String, RUser> users = new HashMap<String, RUser>();
	static String passwdfile = "itools.passwdfile";

	static {
		try {
			// Main.loadProperties(true);
			passwdfile = Main.getProperty("server.resource.save.dir")
					+ "/itools.passwd.txt";
			logger.debug("passwordfile=" + passwdfile);
			loadPassword();
		} catch (Exception e) {
			logger.warn("AccountAdminister init problem", e);
		}
	}

	private static void loadPassword() {
		try {
			FileInputStream input = new FileInputStream(passwdfile);
			List<RUser> list = AccountParser.parse(input);
			for (RUser user : list) {
				users.put(user.getName(), user);
			}
		} catch (Exception e) {
			logger.warn("Password file not exist:" + e);
		}
	}

	private static void save2Disk() {
		try {
			Util.str2File(AccountParser.toXML(users.values()), new File(
					passwdfile));
		} catch (IOException e) {
			logger.warn("save passwd file failed", e);
		}
	}

	public static void save(RUser ruser) {
		users.put(ruser.getName(), ruser);
		save2Disk();
	}

	public static RUser getUser(String user) {
		return (RUser) users.get(user);
	}

	/**
	 * @return array of string which are user's name.
	 */
	public static String[] getAllUsers() {
		return (String[]) users.keySet().toArray(new String[0]);
	}
}
