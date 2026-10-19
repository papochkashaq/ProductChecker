# ProductChecker

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

ProductChecker is a Spring Boot application that periodically monitors product availability on Amazon.in and sends notifications to a Telegram chat when a product comes back in stock.

## Features

- 🔍 Automated product availability monitoring
- 📱 Telegram notifications when products are back in stock
- ⏱️ Smart delay system with exponential backoff on failures
- 🐳 Docker support for easy deployment
- 🔄 Randomized check intervals to avoid detection
- 📊 Comprehensive logging with rotation

## How It Works

1. On startup, [`MonitoringScheduler`](src/main/java/org/example/productchecker/scheduler/MonitoringScheduler.java:13) schedules the first check cycle.
2. Each cycle, [`ProductCheckerService.runCheck()`](src/main/java/org/example/productchecker/service/ProductCheckerService.java:42) shuffles the configured product list and checks each product one by one.
3. For every product, [`AmazonInProductChecker`](src/main/java/org/example/productchecker/checker/AmazonInProductChecker.java) fetches the product page via [`JsoupHtmlFetcher`](src/main/java/org/example/productchecker/scraper/JsoupHtmlFetcher.java) and parses it with [`AmazonInHtmlParser`](src/main/java/org/example/productchecker/scraper/AmazonInHtmlParser.java) to determine its [`ProductStatus`](src/main/java/org/example/productchecker/model/ProductStatus.java).
4. When a product becomes available, a message is sent to Telegram via [`TelegramClient`](src/main/java/org/example/productchecker/telegram/TelegramClient.java).
5. After each cycle, the next run is scheduled with a randomized delay produced by [`DelayGenerator`](src/main/java/org/example/productchecker/scheduler/DelayGenerator.java). If a check fails (e.g., the page cannot be fetched), the delay increases progressively to avoid being blocked.

## Requirements

- Java 25
- Gradle (the included wrapper works out of the box)

## Configuration

Create a `.env` file based on [`.env.example`](.env.example) or set the following environment variables:

| Variable | Description |
|---|---|
| `TELEGRAM_BOT_TOKEN` | Telegram Bot API token |
| `TELEGRAM_CHAT_ID` | Telegram chat ID to send notifications to |

### Key settings

```yaml
app:
  html-fetcher:
    user-agent: Mozilla/5.0 ...   # User-Agent header used for requests
    timeout: 6000                 # HTTP timeout in milliseconds
  monitoring:
    base-min-delay: 4m            # Minimum delay between check cycles
    base-max-delay: 6m            # Maximum delay between check cycles
    delay-after-one-failure: 5m   # Delay after 1 consecutive failure
    delay-after-two-failures: 12m # Delay after 2 consecutive failures
    delay-after-many-failures: 16m# Delay after 3+ consecutive failures
    between-products-min: 15s     # Minimum pause between products in one cycle
    between-products-max: 25s     # Maximum pause between products in one cycle
  product:
    products:                     # List of products to monitor
      - title: Example Product 1
        link: https://www.amazon.in/example-product-1/dp/EXAMPLE1
      - title: Example Product 2
        link: https://www.amazon.in/example-product-2/dp/EXAMPLE2
  telegram:
    token: ${TELEGRAM_BOT_TOKEN}
    chat-id: ${TELEGRAM_CHAT_ID}
```

You can customize the product list in [`application.yaml`](src/main/resources/application.yaml) by adding your own Amazon.in product links.

## Running

### Local Development

```bash
# Linux/macOS
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

Make sure `TELEGRAM_BOT_TOKEN` and `TELEGRAM_CHAT_ID` are set in the environment or in a `.env` file before starting the application.

### Docker

```bash
# Build the application
./gradlew build

# Build Docker image
docker build -t product-checker:latest .

# Run with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f
```

## Building

```bash
./gradlew build
```

The executable JAR is produced in `build/libs/`.

## Testing

```bash
./gradlew test
```

Tests cover the HTML parser (using saved page snapshots in `src/test/resources`), the product checker, the delay generator, and the service logic.

## Project Structure

```
src/main/java/org/example/productchecker/
├── ProductCheckerApplication.java  # Spring Boot entry point
├── checker/    # Product availability checkers (Amazon.in implementation)
├── config/     # Configuration properties classes
├── exception/  # Custom exceptions (PageFetchException)
├── model/      # Domain models (ProductCard, CheckResult, ProductStatus)
├── scheduler/  # Cycle scheduling and delay generation
├── scraper/    # HTML fetching (Jsoup) and parsing
├── service/    # Core checking orchestration
└── telegram/   # Telegram Bot API client
```

## Logging

Logs are written to `logs/product-checker.log` with daily rotation (compressed archives are kept alongside it).

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built with [Spring Boot](https://spring.io/projects/spring-boot)
- HTML parsing powered by [Jsoup](https://jsoup.org/)
- Telegram Bot API for notifications
