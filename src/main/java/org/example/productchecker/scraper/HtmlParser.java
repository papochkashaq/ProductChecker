package org.example.productchecker.scraper;

import org.example.productchecker.model.CheckResult;

public interface HtmlParser {

    CheckResult parse(String html, String url);
}
