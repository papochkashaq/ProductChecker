package org.example.productchecker.scraper;

import org.example.productchecker.config.HtmlFetcherProperties;
import org.example.productchecker.exception.PageFetchException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;

@Component
public class JsoupHtmlFetcher implements HtmlFetcher {

    private static final Logger logger = LoggerFactory.getLogger(JsoupHtmlFetcher.class);

    private final SessionManager sessionManager;
    private final HtmlFetcherProperties properties;

    public JsoupHtmlFetcher(SessionManager sessionManager, HtmlFetcherProperties properties) {
        this.sessionManager = sessionManager;
        this.properties = properties;
    }

    @Override
    public String fetchHtml(String url) {
        try {
            Connection connection = Jsoup.connect(url)
                    .cookies(sessionManager.getCookies())
                    .userAgent(properties.userAgent())
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Referer", "https://www.google.com/")
                    .timeout(properties.timeout())
                    .followRedirects(true)
                    .ignoreHttpErrors(true);
            logger.debug("Old cookies: {}", sessionManager.getCookies());

            Connection.Response response = connection.execute();
            int statusCode = response.statusCode();
            if (statusCode != 200) {
                logger.error("Server error: {}", statusCode);
                throw new PageFetchException("HTTP " + statusCode + " for " + url, null);
            }

            String html = response.body();

            if (html.contains("validateCaptcha")) {
                throw new PageFetchException("Captcha is captured.");
            }

            sessionManager.updateCookies(response.cookies());
            logger.debug("New cookies: {}", sessionManager.getCookies());

            return html;
        } catch (IOException e) {
            logger.error("Connection error: {}", e.getMessage());
            throw new PageFetchException("Connection error: " + url, e);
        } catch (UncheckedIOException e) {
            logger.error("Timeout error: {}", e.getMessage());
            throw new PageFetchException("Timeout error: " + url, e);
        }
    }
}
