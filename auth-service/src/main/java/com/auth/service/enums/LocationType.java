package com.auth.service.enums;

public enum LocationType implements DescribableEnum{
    OUTLET("Outlet"),
    WAREHOUSE("Warehouse");

    private final String description;

    LocationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
