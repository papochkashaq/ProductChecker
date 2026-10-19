package org.example.productchecker.service;

import org.example.productchecker.checker.ProductChecker;
import org.example.productchecker.config.ProductListProperties;
import org.example.productchecker.config.ProductProperties;
import org.example.productchecker.exception.PageFetchException;
import org.example.productchecker.model.CheckResult;
import org.example.productchecker.model.ProductStatus;
import org.example.productchecker.scheduler.DelayGenerator;
import org.example.productchecker.telegram.TelegramClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCheckerServiceTest {

    @Mock
    private TelegramClient telegramClient;

    @Mock
    private ProductChecker productChecker;

    @Mock
    private DelayGenerator delayGenerator;

    private ProductCheckerService service;

    @BeforeEach
    void setUp() {
        ProductListProperties productListProperties = new ProductListProperties(
                List.of(new ProductProperties("Product 1", "https://test1"),
                        new ProductProperties("Product 2", "https://test2")));
        service = new ProductCheckerService(productChecker, telegramClient, productListProperties, delayGenerator);
    }

    @Test
    void shouldSendNotificationWhenProductAppearsInStock() {
        CheckResult inStock = new CheckResult("Product 1", "https://test", ProductStatus.IN_STOCK);

        when(productChecker.checkProduct(anyString())).thenReturn(inStock);
        service.runCheck();

        verify(telegramClient, atLeastOnce()).sendMessage(contains("is available"));
    }

    @Test
    void shouldNotSendNotificationWhenProductWasAlreadyInStock() {
        CheckResult inStock = new CheckResult("Product 1", "https://test", ProductStatus.IN_STOCK);

        when(productChecker.checkProduct(anyString())).thenReturn(inStock);
        service.runCheck();

        verify(telegramClient, times(2)).sendMessage(anyString());
        service.runCheck();

        verify(telegramClient, times(2)).sendMessage(anyString());
    }

    @Test
    void shouldAbortCycleOnFailure() {
        when(productChecker.checkProduct(anyString())).thenThrow(PageFetchException.class);

        service.runCheck();

        verify(productChecker, times(1)).checkProduct(anyString());
        assertEquals(1, service.getFailuresInRow());
    }

    @Test
    void shouldSendNotificationAfterThreeFailures() {
        when(productChecker.checkProduct(anyString())).thenThrow(PageFetchException.class);

        service.runCheck();
        service.runCheck();
        service.runCheck();

        verify(productChecker, times(3)).checkProduct(anyString());
        assertEquals(3, service.getFailuresInRow());
        verify(telegramClient).sendMessage(contains("in a row"));
    }

    @Test
    void shouldResetFailuresAfterSuccess() {
        CheckResult outOfStock = new CheckResult("Product 1", "https://test", ProductStatus.OUT_OF_STOCK);

        when(productChecker.checkProduct(anyString())).thenThrow(PageFetchException.class).thenReturn(outOfStock);
        service.runCheck();
        service.runCheck();

        assertEquals(0, service.getFailuresInRow());
    }


}
