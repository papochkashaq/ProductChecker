package org.example.productchecker.scraper;

import org.example.productchecker.model.CheckResult;
import org.example.productchecker.model.ProductStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AmazonInHtmlParser implements HtmlParser {

    private static final Logger logger = LoggerFactory.getLogger(AmazonInHtmlParser.class);

    @Override
    public CheckResult parse(String html, String url) {
        Document doc = Jsoup.parse(html);
        ProductStatus status = checkAvailability(doc);
        logger.info("Status: {}", status);
        return new CheckResult(doc.title(), url, status);
    }

    private ProductStatus checkAvailability(Document doc) {
        Element cartButton = doc.getElementById("add-to-cart-button");
        Element buyNowButton = doc.getElementById("buy-now-button");
        Element availability = doc.selectFirst("#availability");
        String availabilityText = availability != null ? availability.text() : "";
        Element seeAllBuyingOptions = doc.selectFirst("a:contains(See all buying options), button:contains(See all buying options)");

        boolean hasSeeAllBuyingOptionsSignal = seeAllBuyingOptions != null;
        boolean hasInStockSignal = (cartButton != null ||
            buyNowButton != null ||
            availabilityText.contains("In stock"));
        boolean hasOutOfStockSignal = availabilityText.contains("Currently unavailable.");

        logger.debug("Cart button present: {}", cartButton != null);
        logger.debug("Buy now button present: {}", buyNowButton != null);
        logger.debug("See all buying options present: {}", seeAllBuyingOptions);
        logger.debug("Availability text: {}", availabilityText);

        if (hasInStockSignal || hasSeeAllBuyingOptionsSignal) {
            return ProductStatus.IN_STOCK;
        }
        if (hasOutOfStockSignal) {
            return ProductStatus.OUT_OF_STOCK;
        }

        logger.warn("Unknown status for product: {}", doc.title());
        return ProductStatus.UNKNOWN;

    }
}
