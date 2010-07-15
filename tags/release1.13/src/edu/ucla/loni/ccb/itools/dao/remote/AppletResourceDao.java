package edu.ucla.loni.ccb.itools.dao.remote;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Properties;

import edu.ucla.loni.ccb.itools.dao.ResourceDao;
import edu.ucla.loni.ccb.itools.dao.Security;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.model.RUser;
import edu.ucla.loni.ccb.itools.parser.NcbcResourceParser;
import edu.ucla.loni.ccb.itools.view.GuiUtil;

public class AppletResourceDao implements ResourceDao {
	public void updateResource(NcbcResource rsc) {
		saveResource("update", rsc);
	}
	public void addResource(NcbcResource rsc) {
		saveResource("add", rsc);
	}

	private void saveResource(String method,NcbcResource rsc) {
		if (rsc.isInSandBox()) {
			if (!Security.hasPermission(RUser.NORMALUSER)) {
				GuiUtil.showMsg("You don't have permission to save the resource.");
				return;
			}
		} else if (!Security.hasPermission(RUser.EXPERTUSER)) {
			GuiUtil.showMsg("You don't have permission to save the resource.");
			return;
		}

		String str = NcbcResourceParser.getSaveString(rsc);
		Properties props = new Properties();
		props.put("method", method);
		props.put("center", rsc.getCenter());
		props.put("rsc", str);
		ProxyServer.talk2HttpsServer("restrictedResource.htm", props);
	}
	
	public void removeResource(NcbcResource rsc) {
		String str = NcbcResourceParser.getSaveString(rsc);
		Properties props = new Properties();
		props.put("method", "remove");
		props.put("center", rsc.getCenter());
		props.put("rsc", str);

		ProxyServer.talk2HttpsServer("restrictedResource.htm", props);
	}

	public List<NcbcResource> getResources(String reg, String category) {
		return getResources(reg, category, null);
	}

	public List<NcbcResource> getResources(String reg, String category, String center) {
		String str = "resource.htm" ; 
		Properties props = new Properties();
		props.put("method", "getResources");
		props.put("reg", reg);
		props.put("category", category);
		if (center != null)	{
			props.put("center",center);
		}


		String str0 = ProxyServer.talk2Server(str, props);
		return NcbcResourceParser.parse(new ByteArrayInputStream(str0.getBytes()));
	}
	
	public List<NcbcResource> getAllResources() {
		return getResources("*", "*");
	}

	public List<NcbcResource> load(String center) {
		String urlstr = "resource.htm?method=load";
		Properties props = new Properties();
		    	
		if (center.indexOf(' ') != -1) {
			props.put("center", center);
		} else {
			urlstr +=  "&center=" + center;
		}
		String str0 = ProxyServer.talk2Server(urlstr, props);
		List<NcbcResource> rv = NcbcResourceParser.parse(new ByteArrayInputStream(str0.getBytes()));
		if (center.startsWith("Alb")) {
			System.out.println(rv.size() +" resources parsed from return for cetner ALb\n" + str0);
		}
		return rv;
	}
	
	public void setResources(List<NcbcResource> rscs) {
		if (rscs.size() == 0) {
			return;
		}
		Properties props = new Properties();
		props.put("method", "setResources");
		props.put("rscs", NcbcResourceParser.getSaveString(rscs));

		String str = "restrictedResource.htm";
		ProxyServer.talk2HttpsServer(str, props);
	}
	

	public boolean canRecover(String center) {
		Properties props = new Properties();
		props.put("method", "canRecover");
		props.put("center", center);
		String str = "restrictedResource.htm";
		String rv = ProxyServer.talk2HttpsServer(str, props);
		System.out.println("canRecover return:" + rv);
		return Boolean.valueOf(rv).booleanValue();
	}

	public List<NcbcResource> recover(String center) {
		Properties props = new Properties();
		props.put("method", "recover");
		props.put("center", center);
		String str = "restrictedResource.htm";
		String str0 = ProxyServer.talk2HttpsServer(str, props);
		return NcbcResourceParser.parse(new ByteArrayInputStream(str0.getBytes()));
	}
	public String getOntologyString() {
		return ProxyServer.talk2Server("resource.htm?method=getOntology");
	}
		
	public void setOntologyString(String ontology) {
		Properties props = new Properties();
		props.put("method", "setOntology");
		props.put("ontology", ontology);
		String str = "restrictedResource.htm";
		ProxyServer.talk2HttpsServer(str, props);
	}
}
