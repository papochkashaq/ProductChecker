package org.example.productchecker.checker;

import org.example.productchecker.exception.PageFetchException;
import org.example.productchecker.model.CheckResult;
import org.example.productchecker.scraper.HtmlFetcher;
import org.example.productchecker.scraper.HtmlParser;

import org.springframework.stereotype.Component;

@Component
public class AmazonInProductChecker implements ProductChecker {

    private final HtmlFetcher fetcher;
    private final HtmlParser parser;

    public AmazonInProductChecker(HtmlFetcher fetcher, HtmlParser parser) {
        this.fetcher = fetcher;
        this.parser = parser;
    }

    @Override
    public CheckResult checkProduct(String url) throws PageFetchException {
        String html = fetcher.fetchHtml(url);
        return parser.parse(html, url);
    }
}
