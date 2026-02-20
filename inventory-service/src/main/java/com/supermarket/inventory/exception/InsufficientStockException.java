package com.supermarket.inventory.exception;

import java.util.Map;

public class InsufficientStockException extends RuntimeException {

    private final Map<String, String> insufficientItems;

    public InsufficientStockException(String message, Map<String, String> insufficientItems) {
        super(message);
        this.insufficientItems = insufficientItems;
    }

    public Map<String, String> getInsufficientItems() {
        return insufficientItems;
    }
}
