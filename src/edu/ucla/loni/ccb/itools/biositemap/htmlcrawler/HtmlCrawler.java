package edu.ucla.loni.ccb.itools.biositemap.htmlcrawler;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import de.phleisch.app.itsucks.core.Dispatcher;
import de.phleisch.app.itsucks.filter.download.impl.DownloadJobFilter;
import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.DownloadJobFactory;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;

public class HtmlCrawler {

	private static Log mLog = LogFactory.getLog(HtmlCrawler.class);

	private static HashMap<String, CrawlerWebSearchResult> biositeMapUrls = new HashMap<String, CrawlerWebSearchResult>();

	private static HashMap<String, CrawlerWebSearchResult> biositeRdfMapUrls = new HashMap<String, CrawlerWebSearchResult>();

	static String initialUrl = "http://www.biositemap.org/";
	static String[] allowedHostNames = new String[] { ".*" };
	static String[] save2DistFilter = new String[] { ".*rdf" };
	static int recursiveDepth = 3;
	// when 2, job is in the list but not written to FS.
	// when 3. the rdf was written to the FS and for NCBO_biositemap.rdf it

	// print out twice
	// with State: 60 and 71
	// INFO edu.ucla.loni.ccb.itools.biositemap.ExampleMain: DownloadJob
	// (State: 60, Prio: 500, URL:
	// 'http://www.ncbcs.org/biositemaps/NCBO_biositemap.rdf')
	// INFO edu.ucla.loni.ccb.itools.biositemap.ExampleMain: DownloadJob
	// (State: 71, Prio: 500, URL:
	// 'http://www.ncbcs.org/biositemaps/NCBO_biositemap.rdf')
	static String saveDir = "C:/qma/temp/itsucksrdf";

	Dispatcher dispatcher;
	DownloadJobFactory jobFactory;
public HtmlCrawler()  {
	mLog.info("HtmlCrawler was initiated");
}
	public void craw() {
		biositeMapUrls.clear();
		biositeRdfMapUrls.clear();
		try {
			prepareItSucks();

			mLog.info("iTools dispatcher started Crawling");
			dispatcher.processJobs();

			mLog.info("iTools dispatcher finished Crawling");

			// dump all found URLs
			Collection<Job> content = dispatcher.getJobManager().getJobList()
					.getContent();
			mLog.debug("qma learning: the Joblist are:" + content.size());
			// for (Job finishedJob : content) {
			// mLog.info(finishedJob);
			// }

			mLog.debug("The above is the joblist");
			mLog.info("crawled " + biositeMapUrls.size() + "xmls, and "
					+ biositeRdfMapUrls.size() + " rdf");
		} catch (Exception e) {
			mLog.info(e);
		}

	}

	private void prepareItSucks() throws Exception {
		DownloadJobFilter filter = new DownloadJobFilter();
		filter.setAllowedHostNames(allowedHostNames);
		filter.setMaxRecursionDepth(recursiveDepth);
		filter.setSaveToDisk(save2DistFilter);

		dispatcher.addJobFilter(filter);

		// create an job factory

		// create an initial job
		UrlDownloadJob job = jobFactory.createDownloadJob();
		job.setUrl(new URL(initialUrl));
		
		File tempDir = File.createTempFile("itsucks-download", null);
		if(!tempDir.delete() && !tempDir.mkdir()) {
			throw new RuntimeException("Cannot create tmp directory: " + tempDir);
		}
		
		job.setSavePath(tempDir);

		job.setIgnoreFilter(true);
		dispatcher.addJob(job);
	}

	public static void main(String[] pArgs) {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
				"./axis/WEB-INF/applicationContext-itsucks.xml");
		// ClassPathXmlApplicationContext context = new
		// ClassPathXmlApplicationContext(
		// ApplicationConstants.CORE_SPRING_CONFIG_FILE);

		HtmlCrawler inst = new HtmlCrawler();
		inst.dispatcher = (Dispatcher) context.getBean("Dispatcher");
		inst.jobFactory = (DownloadJobFactory) context.getBean("JobFactory");
		inst.craw();
	}

	public HashMap<String, CrawlerWebSearchResult> getBiositeMapUrls() {
		return biositeMapUrls;
	}

	public HashMap<String, CrawlerWebSearchResult> getBiositeRdfMapUrls() {
		return biositeRdfMapUrls;
	}

	public void setDispatcher(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public void setJobFactory(DownloadJobFactory jobFactory) {
		this.jobFactory = jobFactory;
	}
}
