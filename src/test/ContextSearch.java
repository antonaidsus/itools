package test;
import com.yahoo.search.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;

/**
 * Code sample to demonstrate using the Yahoo! Java API to perform a web
 * search.
 *
 * @author Ryan Kennedy
 */
public class ContextSearch {
    public static void main(String[] args) {
        // Create the search client. Pass it your application ID.
        SearchClient client = new SearchClient("javasdktest");

        // Create the web search request. In this case we're searching for
        // java-related hits.
        String query = "Qunfei Ma";
        ContextSearchRequest request = new ContextSearchRequest(query);
        int returnValue = 100;
        request.setSimilarOk(false);
        request.setContext(query);
        request.setResults(returnValue);
        request.setFormat("html");

        try {
            // Execute the search.
             WebSearchResults results = client.contextWebSearch(request);

            BigInteger totalResultsAvailable = results.getTotalResultsAvailable();
			// Print out how many hits were found.
            System.out.println("Found " + totalResultsAvailable +
                    " hits for " + query + "! Displaying the first " +
                    results.getTotalResultsReturned() + ".");

            WebSearchResult[] results2 = results.listResults();
            workOnResults(results2, 0);
            // Iterate over the results.
        }
        catch (IOException e) {
            // Most likely a network exception of some sort.
            System.err.println("Error calling Yahoo! Search Service: " +
                    e.toString());
            e.printStackTrace(System.err);
        }
        catch (SearchException e) {
            // An issue with the XML or with the service.
            System.err.println("Error calling Yahoo! Search Service: " +
                    e.toString());
            e.printStackTrace(System.err);
        }
    }
    private static HashSet urls = new HashSet();
    protected static void workOnResults(WebSearchResult[] results, int offset) {
        for (int i = 0; i < results.length; i++) {
            WebSearchResult result = results[i];
             
            String url = result.getUrl();
            if (urls.contains(url)) {
            	System.err.println((offset + i + 1) + ", url=" + url + ", was downloaded");
            } else {
            	urls.add(url);
            }
			// Print out the document title and URL.
            System.out.println("   " + (offset + i + 1) + ": " + result.getTitle() + " - " +
                    url);
        }
    }
}