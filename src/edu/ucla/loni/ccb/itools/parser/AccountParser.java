package edu.ucla.loni.ccb.itools.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.Attributes;

import edu.ucla.loni.ccb.itools.model.RUser;

public class AccountParser extends MyXmlParser {
	private Map parsed_users;
	
	public AccountParser() {
		parsed_users = new HashMap();
	}
	
	public Map getUsers() {
		return parsed_users;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) {
		if (qName.equals("user")) {
			RUser u = new RUser();

			for (int i = 0, I = atts.getLength(); i < I; i++) {
				String a = atts.getQName(i);
				String v = atts.getValue(i);
				if (a.equalsIgnoreCase("name")) {
					u.setName(v);
				} else if (a.equalsIgnoreCase("email")) {
					u.setEmail(v);
				} else if (a.equalsIgnoreCase("password")) {
					u.setPasswd(v);
				} else if (a.equalsIgnoreCase("id")) {
					u.setId(Integer.parseInt(v));
				} else if (a.equalsIgnoreCase("role")) {
					if (v.equals("8")) {
						v = RUser.NORMALUSER;
					} else if (v.equals("9")) {
						v = RUser.EXPERTUSER;
					} else if ( v.equals("10")) {
						v = RUser.ADMINISTRATOR;
					}
					u.setRole(v);
				}
			}
			parsed_users.put(u.getName(), u);
		}
	}
	
	public static String toXML(Map users) {
		StringBuffer buf = new StringBuffer("<users>\n");
		for (Iterator iter = users.values().iterator(); iter.hasNext();) {
			RUser element = (RUser) iter.next();
			buf.append(element.toXML(S4));
		}
		buf.append("</users>\n");

		return buf.toString();
	}
}
