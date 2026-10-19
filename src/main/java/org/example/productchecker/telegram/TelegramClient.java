package org.example.productchecker.telegram;

import org.example.productchecker.config.TelegramClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Component
public class TelegramClient {

    private static final Logger logger = LoggerFactory.getLogger(TelegramClient.class);

    private final TelegramClientProperties config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public TelegramClient(TelegramClientProperties config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void sendMessage(String message) {
        String url = config.baseUrl() + config.token() + config.sendMessagePath();
        String json = objectMapper.writeValueAsString(
                Map.of("chat_id", config.chatId(),
                        "text", message));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logger.debug("Telegram response code: {}", response.statusCode());
            logger.debug("Telegram response body: {}", response.body());
        } catch (Exception e) {
            logger.error("Telegram sending message error: {}", e.getMessage());
        }
    }
}
