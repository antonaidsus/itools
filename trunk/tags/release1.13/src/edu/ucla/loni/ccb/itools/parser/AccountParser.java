package edu.ucla.loni.ccb.itools.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import edu.ucla.loni.ccb.itools.model.RUser;

public class AccountParser extends JdomHelper {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(AccountParser.class);

	public static List<RUser> parse(InputStream input) {
		List<RUser> parsed_users = new ArrayList<RUser>();
		try {
			Document document = buildDocument(new InputStreamReader(input));
			Element rootElement = document.getRootElement();
			for (Element ele : getChildren(rootElement)) {
				parsed_users.add(jdom2Ruser(ele));
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
		return parsed_users;
	}

	public static RUser jdom2Ruser(Element ele) {
		RUser u = new RUser();
		u.setName(ele.getAttributeValue("name"));
		u.setEmail(ele.getAttributeValue("email"));
		u.setPasswd(ele.getAttributeValue("password"));
		u.setId(Integer.parseInt(ele.getAttributeValue("id")));
		String v = ele.getAttributeValue("role");

		if (v.equals("8")) {
			v = RUser.NORMALUSER;
		} else if (v.equals("9")) {
			v = RUser.EXPERTUSER;
		} else if (v.equals("10")) {
			v = RUser.ADMINISTRATOR;
		}
		u.setRole(v);

		return u;
	}

	public static Element ruser2Jdom(RUser user) {
		Element ele = new Element("user");
		setAttribute(ele,"name", user.getName());
		setAttribute(ele,"id", user.getId() + "");
		setAttribute(ele,"role", user.getRole());
		setAttribute(ele,"email", user.getEmail());
		setAttribute(ele,"password", user.getPasswd());
		return ele;
	}

	public static String toXML(Collection<RUser> users) {
		Element root = new Element("users");
		for (RUser ruser : users) {	
			root.addContent(ruser2Jdom(ruser));
		}
		return element2String(root);
	}

}
