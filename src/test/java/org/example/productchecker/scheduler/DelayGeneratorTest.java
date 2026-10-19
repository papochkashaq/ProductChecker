package org.example.productchecker.scheduler;

import org.example.productchecker.config.MonitoringTimeProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DelayGeneratorTest {

    private MonitoringTimeProperties timeProperties;

    private DelayGenerator delayGenerator;

    @BeforeEach
    void setUp() {
        timeProperties = new MonitoringTimeProperties(
                Duration.ofMinutes(4),
                Duration.ofMinutes(6),
                Duration.ofMinutes(5),
                Duration.ofMinutes(8),
                Duration.ofMinutes(15),
                Duration.ofSeconds(15),
                Duration.ofSeconds(30)
        );
        delayGenerator = new DelayGenerator(timeProperties);
    }

    @Test
    void shouldReturn5MinWithOneFailure() {
        Duration result = delayGenerator.nextCycleDelay(1);
        assertEquals(Duration.ofMinutes(5), result);
    }

    @Test
    void shouldReturn8MinWithTwoFailures() {
        Duration result = delayGenerator.nextCycleDelay(2);
        assertEquals(Duration.ofMinutes(8), result);
    }

    @Test
    void shouldReturn15MinWithThreeFailures() {
        Duration result = delayGenerator.nextCycleDelay(3);
        assertEquals(Duration.ofMinutes(15), result);
    }

    @Test
    void shouldReturn15MinWithManyFailures() {
        Duration result = delayGenerator.nextCycleDelay(15);
        assertEquals(Duration.ofMinutes(15), result);
    }

    @Test
    void shouldReturnBetweenProductsDelayInRange() {
        for (int i = 0; i < 100; i++) {
            Duration result = delayGenerator.betweenProducts();
            assertThat(result).isBetween(Duration.ofSeconds(15), Duration.ofSeconds(30));
        }
    }

    @Test
    void shouldReturnBaseDelayInRangeWhenNoFailures() {
        for (int i = 0; i < 100; i++) {
            Duration result = delayGenerator.nextCycleDelay(0);
            assertThat(result).isBetween(Duration.ofMinutes(4), Duration.ofMinutes(6));
        }
    }

}
