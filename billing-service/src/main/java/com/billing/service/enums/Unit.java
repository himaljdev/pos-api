package com.billing.service.enums;

public enum Unit implements DescribableEnum {
    KG("kg"),
    G("g"),
    LITER("l"),
    ML("ml"),
    PIECE("piece"),;

    private final String description;

    Unit(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
