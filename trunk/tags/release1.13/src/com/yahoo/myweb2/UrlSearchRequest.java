package com.yahoo.myweb2;

import com.yahoo.rest.RestRequest;

public class UrlSearchRequest extends RestRequest {
    public static final String SORT_BY_DATE = "date";
    public static final String SORT_BY_TITLE = "title";
    public static final String SORT_BY_URL = "url";

    public UrlSearchRequest() {
        super("http://search.yahooapis.com/MyWebService/V1/urlSearch");
    }

    public void setTag(String tag) {
        setParameter("tag", tag);
    }

    public void setYahooId(String yahooId) {
        setParameter("yahooid", yahooId);
    }

    public void setSortOrder(String sortOrder) {
        setParameter("sort", sortOrder);
    }

    public void setSortReverse(boolean reverse) {
        if(reverse) {
            setParameter("reverse_sort", "1");
        }
        else {
            setParameter("reverse_sort", "0");
        }
    }

    public void setResults(int results) {
        setParameter("results", Integer.toString(results));
    }

    public void setStart(int start) {
        setParameter("start", Integer.toString(start));
    }
}