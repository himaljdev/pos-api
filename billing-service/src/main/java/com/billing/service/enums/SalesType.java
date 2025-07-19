package com.billing.service.enums;

public enum SalesType implements DescribableEnum {
    NORMAL("Normal"),
    WHOLESALE("Wholesale"),;

    private final String description;

    SalesType(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}




