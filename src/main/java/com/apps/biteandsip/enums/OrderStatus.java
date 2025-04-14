package com.apps.biteandsip.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum OrderStatus {
    CANCELLED(0, "CANCELLED"),
    RECEIVED(1, "RECEIVED"),
    ACCEPTED(2, "ACCEPTED"),
    PREPARING(3, "PREPARING"),
    READY_FOR_DELIVERY(4, "READY_FOR_DELIVERY"),
    ON_THE_WAY(5, "ON_THE_WAY"),
    DELIVERED(6, "DELIVERED");

    private int id;
    private String description;

    OrderStatus(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public static OrderStatus getStatusByDescription(String description){
        for(OrderStatus status : OrderStatus.values()){
            if(status.getDescription().equalsIgnoreCase(description))
                return status;
        }

        return null;
    }

    public static OrderStatus getStatusById(int id){
        for(OrderStatus status : OrderStatus.values()){
            if(status.getId() == id)
                return status;
        }

        return null;
    }

    public static OrderStatus getNextStatus(String status){
        OrderStatus orderStatus = getStatusByDescription(status);
        if(orderStatus == null || orderStatus.getId() == 6)
            return null;

        return getStatusById(orderStatus.getId()+1);
    }
}
