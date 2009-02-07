package edu.ucla.loni.ccb.itools.dao.file;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.model.RUser;
import edu.ucla.loni.ccb.itools.parser.AccountParser;

/**
 * This class write or read user account to or read from File System.
 * The file can be specified from property file.
 * @author Jeff Qunfei Ma
 *
 */
public class AccountIO{
	public static final Logger LOGGER = Logger.getLogger(AccountIO.class);
	
	static private Map users = new HashMap();
	static String passwdfile = "itools.passwdfile";

	static {		
		try {
			//Main.loadProperties(true);
			passwdfile = Main.getProperty("server.resource.save.dir") + "/itools.passwd.txt";
			LOGGER.debug("passwordfile=" + passwdfile);
			loadPassword();			
		} catch (Exception e) {
			LOGGER.warn("AccountAdminister init problem",e);
		}
	}

	private static void loadPassword() {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			
			FileInputStream input = new FileInputStream(passwdfile);
			AccountParser accountParser = new AccountParser();
			parser.parse(input, accountParser);
			input.close();
			users = accountParser.getUsers();
		} catch (Exception e) {
			LOGGER.warn("Password file not exist:" + e);
		} 
	}

	private static void save2Disk() {
		String string = AccountParser.toXML(users);
		try {
			FileWriter fw = new FileWriter(passwdfile);
			fw.write(string);
			fw.close();
		} catch (Exception e) {
			LOGGER.error("Save password file failed", e);
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
