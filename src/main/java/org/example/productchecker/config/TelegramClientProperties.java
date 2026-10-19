package org.example.productchecker.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.telegram")
@Validated
public record TelegramClientProperties(@NotBlank String token,
                                       @NotBlank String chatId,
                                       @NotBlank String baseUrl,
                                       @NotBlank String sendMessagePath) {
}
