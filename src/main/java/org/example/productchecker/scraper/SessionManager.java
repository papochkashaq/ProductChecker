package org.example.productchecker.scraper;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private Map<String, String> cookies = new ConcurrentHashMap<>();

    public Map<String, String> getCookies() {
        return new HashMap<>(cookies);
    }

    public void updateCookies(Map<String, String> newCookies) {
        this.cookies.putAll(newCookies);
    }
}
