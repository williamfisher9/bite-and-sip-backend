package com.apps.biteandsip.enums;

public enum OrderStatus {
    RECEIVED("Received"),
    ACCEPTED("Accepted"),
    PREPARING("Preparing"),
    READY_FOR_DELIVERY("Ready for Delivery"),
    ON_THE_WAY("On The Way"),
    DELIVERED("Delivered");

    private String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
