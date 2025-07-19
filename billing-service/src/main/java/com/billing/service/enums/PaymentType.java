package com.billing.service.enums;

public enum PaymentType implements DescribableEnum {
    CASH("Cash"),
    CARD("Card");

    private final String description;

    PaymentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}




