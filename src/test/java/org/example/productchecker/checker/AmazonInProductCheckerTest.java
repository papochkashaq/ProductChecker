package org.example.productchecker.checker;

import org.example.productchecker.model.CheckResult;
import org.example.productchecker.model.ProductStatus;
import org.example.productchecker.scraper.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmazonInProductCheckerTest {

    @Mock
    private HtmlFetcher fetcher;
    @Mock
    private HtmlParser parser;
    @InjectMocks
    private AmazonInProductChecker checker;

    @Test
    void shouldReturnCheckResultWithProductStatusInStock() throws Exception {
        String url = "test-url";
        String html = "<html>...</html>";
        CheckResult expected = new CheckResult("title", url, ProductStatus.IN_STOCK);

        when(fetcher.fetchHtml(url)).thenReturn(html);
        when(parser.parse(html, url)).thenReturn(expected);

        CheckResult actual = checker.checkProduct(url);
        verify(fetcher).fetchHtml(url);
        verify(parser).parse(html, url);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnCheckResultWithProductStatusOutOfStock() throws Exception {
        String url = "test-url";
        String html = "<html>...</html>";
        CheckResult expected = new CheckResult("title", url, ProductStatus.OUT_OF_STOCK);

        when(fetcher.fetchHtml(url)).thenReturn(html);
        when(parser.parse(html, url)).thenReturn(expected);

        CheckResult actual = checker.checkProduct(url);
        verify(fetcher).fetchHtml(url);
        verify(parser).parse(html, url);
        assertEquals(expected, actual);
    }

}
