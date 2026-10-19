package org.example.productchecker.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;

@Validated
public record ProductProperties(
        @NotBlank String title,
        @NotBlank  @Pattern(regexp = "^https?://.*", message = "Invalid URL") String link) {
}
