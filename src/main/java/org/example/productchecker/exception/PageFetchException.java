package org.example.productchecker.exception;

public class PageFetchException extends RuntimeException {

    public PageFetchException(String message, Throwable cause) {
        super(message, cause);
    }

    public PageFetchException(String message) {
        super(message);
    }
}
