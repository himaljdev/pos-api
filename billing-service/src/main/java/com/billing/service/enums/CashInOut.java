package com.billing.service.enums;

public enum CashInOut implements DescribableEnum {
    IN("In"),
    OUT("Out");

    private final String description;

    CashInOut(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

