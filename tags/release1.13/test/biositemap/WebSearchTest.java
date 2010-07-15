package biositemap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yahoo.search.CacheInfo;
import com.yahoo.search.WebSearchRequest;
import com.yahoo.search.WebSearchResult;

import edu.ucla.loni.ccb.itools.Main;
import edu.ucla.loni.ccb.itools.biositemap.WebSearch;
import edu.ucla.loni.ccb.itools.dao.DaoFactory;
import edu.ucla.loni.ccb.itools.model.NcbcResource;
import edu.ucla.loni.ccb.itools.parser.RdfParser;

public class WebSearchTest {
	
	static abstract class DumpSearchResult implements WebSearchResult {

		public CacheInfo getCache() {
			return null;
		}

		public String getMimeType() {
			return null;
		}

		public String getModificationDate() {
			return null;
		}

		public String getSummary() {
			return "summary";
		}


		public String getTitle() {
			return "title";
		}



	}
	static WebSearchResult rdfresult = new DumpSearchResult() {
		public String getClickUrl() {
			return "http://simbios.stanford.edu/biositemap.rdf";
		}

		public String getUrl() {
			return "http://simbios.stanford.edu/biositemap.rdf";
		}
		
	};
	
	@BeforeClass
	public static void initProperty() {
		Main.loadProperties(false);
//		Main.putProperty("local.resource.save.dir", "/tmp/iToolsTestData");
//		Main.putProperty("dao.type", "filesystem");
	}
	
	@Test()
	public void testNewRdfFound() {
		String urlStr = rdfresult.getUrl();
		RdfParser parser = new RdfParser();
		try {
			List<NcbcResource> resources = parser.parse(new URL(urlStr).openStream());
			String biositemapName = parser.getName();
			assertNotNull(biositemapName);

//			DaoFactory.getDaoFactory().getCenterDao().addExternalCenter(
//					biositemapName);
			for (NcbcResource rsc : resources) {
				DaoFactory.getDaoFactory().getResourceDao()
						.addResource(rsc);
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}
	/**
	 * the url's rdf has different name of center and base.
	 */
	@Test()
	public void testNewRdfFound1() {
		String urlStr = "http://www.ccmb.med.umich.edu/biositemap-CTSA-Michigan.rdf";
		RdfParser parser = new RdfParser();
		try {
			List<NcbcResource> resources = parser.parse(new URL(urlStr).openStream());
			String biositemapName = parser.getName();
			assertNotNull(biositemapName);
//			assertEquals("CTSA", biositemapName);

			for (NcbcResource rsc : resources) {
				assertEquals(biositemapName, rsc.getCenter());
				DaoFactory.getDaoFactory().getResourceDao()
						.addResource(rsc);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}
	
	@Test(timeout = 12000)
	public void yahooSearch() {
		WebSearch webSearch = new WebSearch();
		WebSearchRequest request = new WebSearchRequest("biosite");
		webSearch.setRequest(request);
		webSearch.setYahooSearchTimeout(8000);
		webSearch.yahooSearch();
	}


}
