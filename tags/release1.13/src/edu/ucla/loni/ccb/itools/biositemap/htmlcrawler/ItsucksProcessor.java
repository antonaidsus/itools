package edu.ucla.loni.ccb.itools.biositemap.htmlcrawler;

import de.phleisch.app.itsucks.job.Job;
import de.phleisch.app.itsucks.job.download.impl.UrlDownloadJob;
import de.phleisch.app.itsucks.processing.DataChunk;
import de.phleisch.app.itsucks.processing.DataProcessorInfo;
import de.phleisch.app.itsucks.processing.ProcessingException;
import de.phleisch.app.itsucks.processing.impl.AbstractDataProcessor;

public class ItsucksProcessor extends AbstractDataProcessor {
	private HtmlCrawler htmlCrawler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.phleisch.app.itsucks.processing.AbstractDataProcessor#supports(de.
	 * phleisch.app.itsucks.Job)
	 */
	@Override
	public boolean supports(Job pJob) {
		if (pJob instanceof UrlDownloadJob) {
			UrlDownloadJob downloadJob = (UrlDownloadJob) pJob;
			String urlStr = downloadJob.getUrl().toString();
			boolean rv = urlStr.toLowerCase().endsWith("biositemap.rdf");
			if (rv) {
				addWebSearchResult(urlStr);
			}
		}

		return false;
	}

	private void addWebSearchResult(String urlStr) {
		CrawlerWebSearchResult rst = new CrawlerWebSearchResult(urlStr);
		getHtmlCrawler().getBiositeRdfMapUrls().put(urlStr, rst);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.processing.AbstractDataProcessor#init()
	 */
	@Override
	public void init() throws ProcessingException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.phleisch.app.itsucks.processing.DataProcessor#getInfo()
	 */
	public DataProcessorInfo getInfo() {

		return new DataProcessorInfo(
				DataProcessorInfo.ResumeSupport.RESUME_SUPPORTED,
				DataProcessorInfo.ProcessorType.CONSUMER,
				DataProcessorInfo.StreamingSupport.STREAMING_SUPPORTED);
	}


	public void resumeAt(long arg0) {
	}

	public DataChunk process(DataChunk arg0) throws ProcessingException {
		return null;
	}

	public void setHtmlCrawler(HtmlCrawler htmlCrawler) {
		this.htmlCrawler = htmlCrawler;
	}

	public HtmlCrawler getHtmlCrawler() {
		return htmlCrawler;
	}

}
