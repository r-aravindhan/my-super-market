package com.supermarket.inventory.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class InsufficientStockException extends RuntimeException {

    private final Map<String, String> insufficientItems;

    public InsufficientStockException(String message, Map<String, String> insufficientItems) {
        super(message);
        this.insufficientItems = insufficientItems;
    }
}
