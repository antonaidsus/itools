
package edu.ucla.loni.ccb.itools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * This is Main class of this project. It mainly creates and shows the Main
 * Frame.
 * 
 * @author Qunfei Ma
 * 
 */
public class Main {
	public static final Logger LOGGER = Logger.getLogger(Main.class);

	// public static final ImageIcon ITOOLS_WATERMARK =
	// GuiUtil.getIcon("/iTool_watermark.jpg");
	private static boolean appletMode = true;
	private static boolean serverMode = false;

	private static String serverUrl;
	private static String secure_serverUrl;
	private static Properties props = new Properties();

	public static boolean isAppletMode() {
		return appletMode;
	}
	
	public static void setAppletMode(boolean b) {
		appletMode = b;
	}
	
	public static void setServerUrl(String parameter) {
		serverUrl = parameter;
		props.put("server.url", parameter);
	}
	
	public static String getServerUrl() {
		return serverUrl;
	}
	
	public static void setSecureServerUrl(String parameter) {
		secure_serverUrl = parameter;
		props.put("secure_server.url", parameter);
	}
	
	public static String getSecureServerUrl() {
		return secure_serverUrl;
	}

	
	public static boolean isServerMode() {
		return serverMode;
	}
	public static void setServerMode(boolean b) {
		serverMode = b;
	}
	
	public static String getProperty(String key) {
		return props.getProperty(key);
	}
	
	public static void putProperty(String key, String value) {
		props.setProperty(key, value);
	}

	public static void loadProperties(boolean serverMode) {
		String name = serverMode ? "itools.server.properties" : "itools.properties";
		InputStream input = Main.class.getResourceAsStream("/" + name);
		if (input != null) {
			LOGGER.debug("properties loaded from:" + name);
			readPropoperties(input);
		}
		input = Main.class.getResourceAsStream("/database.properties");
		if (input != null) {
			LOGGER.debug("properties loaded from: database.properties");
			readPropoperties(input);
		}
		try {
		    props.putAll(System.getProperties());
		    String derby = props.getProperty("derby.system.home");
		    LOGGER.debug("derby.home="+derby);
		    if (derby != null) System.setProperty("derby.system.home",derby);
		} catch( Exception e) {
			LOGGER.warn(e);
		}
	}

	private static void readPropoperties(InputStream input) {
		try {
			props.load(input);
			input.close();
		} catch (IOException e) {
			LOGGER.debug(e);
		}
	}
}
