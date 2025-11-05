package com.example.demo.model.enums;

public enum ShippingMethodType {
    PICKUP("Nhận tại cửa hàng"),
    DELIVERY("Giao tận nơi");

    private final String displayName;

    ShippingMethodType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { // PHẢI public
        return displayName;
    }
}
