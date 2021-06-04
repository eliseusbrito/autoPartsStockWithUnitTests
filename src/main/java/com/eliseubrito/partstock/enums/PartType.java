package com.eliseubrito.partstock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartType {

    IGNITION("Ignition"),
    BATTERIES("Batteries"),
    ENGINE("Engine"),
    FLUIDS("Fluids"),
    TRANSMISSION("Transmission"),
    ELECTRONICS("Electronics"),
    ACCESSORIES("Accessories");

    private final String description;
}
