package edu.ucla.loni.ccb.itools.dao.remote;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.log4j.Logger;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.Util;

/**
 * All clients should use this class to talk to the server.
 * 
 * @author Jeff Qunfei Ma
 * 
 */
public class ProxyServer {
	public static final Logger LOGGER = Logger.getLogger(ProxyServer.class);

	static String s_cookie;

	private static String serverurl = Main.getServerUrl();
	private static String secure_serverurl = Main.getSecureServerUrl();
	private static String webappName = Main.getProperty("webapp.name");

	static {
		if (!serverurl.endsWith("/"))
			serverurl += "/";
		if (!secure_serverurl.endsWith("/")) {
			secure_serverurl += "/";
		}
		try {
			trustAllHttpsCertificates();
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					LOGGER.debug("Warning: URL Host: " + urlHostName + " vs. "
							+ session.getPeerHost());
					return true;
				}
			};

			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception e) {
			LOGGER.warn("static Intilization:", e);
		}
	}

	public static String talk2HttpsServer(String str, Properties props) {
		return doTalk2Server(true, str, props);
	}

	public static String talk2Server(String str, Properties props) {
		return doTalk2Server(false, str,props);

	}
	
	public static String talk2HttpsServer(String str) {
		return doTalk2Server(true, str, new Properties());
	}

	public static String talk2Server(String str) {
		return doTalk2Server(false, str,new Properties());

	}


//	public static String doTalk2Server(boolean https, String str) {
//		String urlStr = (https ? secure_serverurl : serverurl) + webappName + "/"
//				+ str;
//		LOGGER.debug(urlStr);
//		// if (true) return doTalk2Server0(urlStr);
//		try {
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			URL url = new URL(urlStr);
//			URLConnection connection = url.openConnection();
//			if (https && s_cookie != null) {
//				connection.setRequestProperty("cookie", s_cookie);
//			}
//			connection.setUseCaches(false);
//			connection.setDoInput(true);
//
//			InputStream openStream = connection.getInputStream();
//
//			echoData(out, openStream);
//			openStream.close();
//			out.close();
//
//			if (https && s_cookie == null) {
//				String cookie = connection.getHeaderField("set-cookie");
//				if (cookie != null) {
//					s_cookie = parseCookie(cookie);
//					LOGGER.debug("sessionID=" + s_cookie);
//				}
//			}
//			String toString = out.toString();
//			LOGGER.debug(toString);
//			return toString;
//		} catch (Exception e) {
//			Main.LOGGER.warn(e, e);
//		}
//		return "";
//	}
	
	public static String getFullUrl(String path, boolean https) {
		String urlStr = (https ? secure_serverurl : serverurl) + webappName + "/"
		+ path;
		
		return urlStr;
	}
	
	public static String doTalk2Server(boolean https, String str, Properties props) {
		String urlStr = getFullUrl(str, https);
		Object center = props.get("center");
		String logStr = urlStr;
		if (center != null) {
			logStr += "&center=" + center;
		}
		LOGGER.debug(logStr);
		//use httpMessage()
		try {
			 URL url = new URL(urlStr);
			  
			 HttpMessage msg = new HttpMessage(url);
			  
			 // Parameters may optionally be set using java.util.Properties
			 // Headers, cookies, and authorization may be set as well
				if (https && s_cookie != null) {
					msg.setCookie(s_cookie);
				}

			InputStream in = msg.sendPostMessage(props);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Util.echoData(out, in);
			in.close();
			out.close();

			String toString = out.toString();
//			LOGGER.debug(toString);
			return toString;
		} catch (Exception e) {
			LOGGER.warn(urlStr + " get Exception: ",  e);
		}
		return "";
	}


	static String parseCookie(String cookie) {
		String rv = cookie;
		int index = cookie.indexOf(";");
		if (index >= 0) {
			rv = cookie.substring(0, index);
		}
		return rv;
	}

	private static void trustAllHttpsCertificates() throws Exception {

		// Create a trust manager that does not validate certificate chains:

		javax.net.ssl.TrustManager[] trustAllCerts =

		new javax.net.ssl.TrustManager[1];

		javax.net.ssl.TrustManager tm = new miTM();

		trustAllCerts[0] = tm;

		javax.net.ssl.SSLContext sc =

		javax.net.ssl.SSLContext.getInstance("SSL");

		sc.init(null, trustAllCerts, null);

		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(

		sc.getSocketFactory());

	}

	public static class miTM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}

}
