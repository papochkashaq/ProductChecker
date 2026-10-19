package org.example.productchecker.service;

import org.example.productchecker.config.ProductListProperties;
import org.example.productchecker.exception.PageFetchException;
import org.example.productchecker.model.CheckResult;
import org.example.productchecker.model.ProductCard;
import org.example.productchecker.model.ProductStatus;
import org.example.productchecker.checker.ProductChecker;
import org.example.productchecker.scheduler.DelayGenerator;
import org.example.productchecker.telegram.TelegramClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ProductCheckerService {

    private static final Logger logger = LoggerFactory.getLogger(ProductCheckerService.class);
    private static final int NOTIFICATION_THRESHOLD = 3;

    private final ProductChecker productChecker;
    private final TelegramClient telegramClient;
    private final List<ProductCard> products;
    private final DelayGenerator delayGenerator;

    private int failuresInRow = 0;

    public ProductCheckerService(ProductChecker productChecker, TelegramClient telegramClient, ProductListProperties productListProperties, DelayGenerator delayGenerator) {
        this.productChecker = productChecker;
        this.telegramClient = telegramClient;
        this.delayGenerator = delayGenerator;
        this.products = productListProperties.products().stream()
                .map(ProductCard::new)
                .toList();
    }

    public void runCheck() {
        logger.info("Number of products: {}", products.size());
        List<ProductCard> shuffled = new ArrayList<>(products);
        Collections.shuffle(shuffled);
        for (int i = 0; i < shuffled.size(); i++) {
            checkOne(shuffled.get(i));

            if (failuresInRow > 0) {
                handleFailure();
                break;
            }

            if (i < products.size() - 1) {
                sleepBetweenProducts();
            }
        }
        logger.debug("All products in the list checked.");
    }

    public int getFailuresInRow() {
        return failuresInRow;
    }

    private void checkOne(ProductCard product) {
        logger.info("Checking product: {}", product.getTitle());
        CheckResult result;
        try {
            result = productChecker.checkProduct(product.getLink());
        } catch (PageFetchException e) {
            logger.warn("Connection try failed: {}", e.getMessage());
            failuresInRow++;
            logger.warn("There are {} fails in a row", failuresInRow);
            return;
        }
        failuresInRow = 0;
        ProductStatus newStatus = result.status();
        boolean appeared = newStatus == ProductStatus.IN_STOCK && product.getLastStatus() != ProductStatus.IN_STOCK;
        product.setLastStatus(newStatus);

        if (appeared) {
            logger.info("Product in stock! Send notification.");
            sendNotification(result);
        }
    }

    private void sendNotification(CheckResult result) {
        String message = String.format("""
                %s is available!
                
                Buy now: %s
                """, result.title(), result.url());
        telegramClient.sendMessage(message);
    }

    private void handleFailure() {
        logger.warn("Cycle aborted after {} failure(s), scheduler will back off.", failuresInRow);
        if (failuresInRow >= NOTIFICATION_THRESHOLD) {
            telegramClient.sendMessage(String.format("Connection fails %d times in a row", failuresInRow));
        }
    }

    private void sleepBetweenProducts() {
        Duration delay = delayGenerator.betweenProducts();
        logger.debug("Delay is: {}s", delay.getSeconds());
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            logger.error("Thread was interrupted.", e);
            Thread.currentThread().interrupt();
        }
    }
}
