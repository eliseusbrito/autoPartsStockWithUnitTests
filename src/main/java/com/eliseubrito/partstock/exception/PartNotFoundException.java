package com.eliseubrito.partstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PartNotFoundException extends Exception {

    public PartNotFoundException(String partName) {
        super(String.format("Auto part with name %s not found in the system.", partName));
    }

    public PartNotFoundException(Long id) {
        super(String.format("Auto part with id %s not found in the system.", id));
    }
}
