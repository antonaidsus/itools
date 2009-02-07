package test;
import com.yahoo.search.*;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.HashSet;

/**
 * Code sample to demonstrate using the Yahoo! Java API to perform a web
 * search.
 *
 * @author Ryan Kennedy
 */
public class WebSearch {
    public static void main(String[] args) {
        // Create the search client. Pass it your application ID.
        SearchClient client = new SearchClient("javasdktest");

        // Create the web search request.
        String query = "websphere";
        WebSearchRequest request = new WebSearchRequest(query);
        int returnValue = 100;
        request.setResults(returnValue);
        request.setFormat("html");
        request.setType("all");

        try {
            WebSearchResults results = client.webSearch(request);

            BigInteger totalResultsAvailable = results.getTotalResultsAvailable();
            System.out.println("Found " + totalResultsAvailable +
                    " hits for " + query + "! Displaying the first " +
                    results.getTotalResultsReturned() + ".");

            WebSearchResult[] results2 = results.listResults();
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
    
    private static boolean isBioSiteMapUrl(String urlStr) {
    	return true;
    }
    private static HashSet urls = new HashSet();
    
 
}