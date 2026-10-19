package org.example.productchecker.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.monitoring")
@Validated
public record MonitoringTimeProperties(@NotNull Duration baseMinDelay,
                                       @NotNull Duration baseMaxDelay,
                                       @NotNull Duration delayAfterOneFailure,
                                       @NotNull Duration delayAfterTwoFailures,
                                       @NotNull Duration delayAfterManyFailures,
                                       @NotNull Duration betweenProductsMin,
                                       @NotNull Duration betweenProductsMax) {
}
