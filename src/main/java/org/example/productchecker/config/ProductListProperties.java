package org.example.productchecker.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "app.product")
@Validated
public record ProductListProperties(@NotEmpty List<ProductProperties> products) {
}
