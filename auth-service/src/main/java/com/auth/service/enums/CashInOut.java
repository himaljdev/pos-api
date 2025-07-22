package com.auth.service.enums;

public enum CashInOut implements DescribableEnum {
    IN("In"),
    OUT("Out"),
    OP("Opening Balance");

    private final String description;

    CashInOut(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

