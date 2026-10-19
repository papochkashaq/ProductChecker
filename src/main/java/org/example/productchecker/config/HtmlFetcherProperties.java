package org.example.productchecker.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.html-fetcher")
@Validated
public record HtmlFetcherProperties(@NotBlank String userAgent,
                                    @NotNull int timeout) {
}
