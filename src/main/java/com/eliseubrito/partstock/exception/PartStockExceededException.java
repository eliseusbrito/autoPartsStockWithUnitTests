package com.eliseubrito.partstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PartStockExceededException extends Exception {

    public PartStockExceededException(Long id, int quantityToIncrement) {
        super(String.format("Auto Parts with %s ID to increment informed exceeds the max stock capacity: %s", id, quantityToIncrement));
    }
}
