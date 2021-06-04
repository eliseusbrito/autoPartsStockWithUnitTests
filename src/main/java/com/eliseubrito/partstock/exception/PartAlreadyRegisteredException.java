package com.eliseubrito.partstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PartAlreadyRegisteredException extends Exception{

    public PartAlreadyRegisteredException(String partName) {
        super(String.format("Auto Part with name %s already registered in the system.", partName));
    }
}
