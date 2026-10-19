package org.example.productchecker.scheduler;

import org.example.productchecker.config.MonitoringTimeProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DelayGenerator {

    private final MonitoringTimeProperties timeProperties;

    public DelayGenerator(MonitoringTimeProperties timeProperties) {
        this.timeProperties = timeProperties;
    }

    public Duration betweenProducts() {
        return randomBetween(timeProperties.betweenProductsMin(), timeProperties.betweenProductsMax());
    }

    public Duration nextCycleDelay(int failuresInRow) {
        Duration base = base();
        return switch (failuresInRow) {
            case 0 -> base;
            case 1 -> timeProperties.delayAfterOneFailure();
            case 2 -> timeProperties.delayAfterTwoFailures();
            default -> timeProperties.delayAfterManyFailures();
        };
    }

    private Duration base() {
        return randomBetween(timeProperties.baseMinDelay(), timeProperties.baseMaxDelay());
    }

    private Duration randomBetween(Duration min, Duration max) {
        long seconds = ThreadLocalRandom.current().nextLong(min.getSeconds(), max.getSeconds() + 1);
        return Duration.ofSeconds(seconds);
    }
}
