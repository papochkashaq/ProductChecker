FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

RUN groupadd -r productchecker && useradd -r -g productchecker productchecker
RUN mkdir -p /var/log/product-checker && chown productchecker:productchecker /var/log/product-checker

COPY build/libs/*.jar app.jar

USER productchecker

VOLUME /var/log/product-checker

ENTRYPOINT ["java", "-jar", "app.jar"]
