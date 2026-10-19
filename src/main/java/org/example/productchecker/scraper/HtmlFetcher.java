package org.example.productchecker.scraper;

import org.example.productchecker.exception.PageFetchException;

public interface HtmlFetcher {

    String fetchHtml(String url) throws PageFetchException;
}
