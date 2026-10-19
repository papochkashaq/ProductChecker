package org.example.productchecker.scraper;

import org.example.productchecker.model.CheckResult;
import org.example.productchecker.model.ProductStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AmazonInHtmlParserTest {

    private static final String IN_STOCK_FILE = "page-in-stock.html";
    private static final String OUT_OF_STOCK_FILE = "page-out-of-stock.html";

    private final HtmlParser parser = new AmazonInHtmlParser();

    @Test
    void shouldReturnInStockWhenProductIsAvailable() throws IOException {
        String html = loadResource(IN_STOCK_FILE);

        CheckResult result = parser.parse(html, "https://test");

        assertEquals(ProductStatus.IN_STOCK, result.status());
        assertEquals("https://test", result.url());
        assertNotNull(result.title());
        assertFalse(result.title().isBlank());
    }

    @Test
    void shouldReturnOutOfStockWhenProductIsNotAvailable() throws IOException {
        String html = loadResource(OUT_OF_STOCK_FILE);

        CheckResult result = parser.parse(html, "https://test");

        assertEquals(ProductStatus.OUT_OF_STOCK, result.status());
        assertEquals("https://test", result.url());
        assertNotNull(result.title());
        assertFalse(result.title().isBlank());
    }

    @Test
    void shouldReturnUnknownStatusWhenPageIsNotStandart() {
        String html = "<html><body><div>nothing familiar</div></body></html>";

        CheckResult result = parser.parse(html, "https://test");

        assertEquals(ProductStatus.UNKNOWN, result.status());
        assertEquals("https://test", result.url());
    }

    @Test
    void shouldDetectInStockWhenUnavailableTextPresent() {
        String html = "<html><body><button id='add-to-cart-button'>Add</button><div>Currently unavailable.</div></body></html>";

        CheckResult result = parser.parse(html, "https://test");

        assertEquals(ProductStatus.IN_STOCK, result.status());
        assertEquals("https://test", result.url());
        assertNotNull(result.title());
    }

    @Test
    void shouldReturnInStockWhenOnlyBuyNowButtonPresent() {
        String html = "<html><body><input id='buy-now-button'></body></html>";

        CheckResult result = parser.parse(html, "https://test");

        assertEquals(ProductStatus.IN_STOCK, result.status());
    }

    @Test
    void shouldReturnInStockWhenInStockTextPresent() {
        String html = "<html><body><div id='availability'>In stock</div></body></html>";

        CheckResult result = parser.parse(html, "https://test");

        assertEquals(ProductStatus.IN_STOCK, result.status());
    }

    @Test
    void shouldReturnOutOfStockWhenOnlyUnavailableTextPresent() {
        String html = "<html><body><div id='availability'>Currently unavailable.</div></body></html>";

        CheckResult result = parser.parse(html, "https://test");

        assertEquals(ProductStatus.OUT_OF_STOCK, result.status());
    }

    private String loadResource(String file) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(file)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + file);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}
